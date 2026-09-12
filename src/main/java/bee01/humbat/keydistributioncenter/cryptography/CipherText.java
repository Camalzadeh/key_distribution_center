package bee01.humbat.keydistributioncenter.cryptography;

import java.util.Base64;

/**
 * Makes a symmetric ciphertext safe to put in a database column.
 *
 * <p>The classical ciphers here work on {@code char} values and produce
 * whatever the arithmetic gives them. That includes {@code U+0000}, and a NUL
 * cannot be stored in a PostgreSQL {@code text} column at all:
 *
 * <pre>
 *   ERROR: invalid byte sequence for encoding "UTF8": 0x00
 * </pre>
 *
 * <p>Sending a message therefore failed with a 500 roughly seven times in a
 * hundred, depending entirely on which text met which key — measured on
 * 2026-09-12 across ten texts, two ciphers and five block modes. Caesar and
 * Vigen&egrave;re shift and XOR, so they can land on 0x00; Playfair and Rail
 * Fence only rearrange letters, and never could.
 *
 * <p>NUL is not the only hazard. XOR-based block modes can also produce a
 * value in the surrogate range {@code U+D800..U+DFFF}, which is not a valid
 * code point on its own — encoding such a string to UTF-8 silently replaces
 * it, so the ciphertext would come back subtly wrong rather than failing
 * loudly. Both problems have the same cause: cipher output is a sequence of
 * 16-bit units, not text.
 *
 * <p>So it is not stored as text. Each char is written as two bytes and the
 * result is Base64-encoded, which round-trips every possible char exactly.
 */
public final class CipherText {

    /**
     * Marks the encoded form. Without it, telling an encoded value from a
     * legacy one would mean guessing whether a string "looks like" Base64 —
     * and cipher output can look like anything, including valid Base64.
     */
    private static final String PREFIX = "b64:";

    private CipherText() {
    }

    /** Encodes cipher output for storage. */
    public static String store(String raw) {
        if (raw == null) {
            return null;
        }
        byte[] bytes = new byte[raw.length() * 2];
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            bytes[i * 2] = (byte) (c >>> 8);
            bytes[i * 2 + 1] = (byte) c;
        }
        return PREFIX + Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decodes a stored value back to cipher output.
     *
     * <p>A value without the prefix is returned unchanged. Those are rows
     * written before this class existed, and they are readable for the same
     * reason they were storable at all: they happened to contain no NUL. This
     * branch exists only for them and can be deleted once none are left.
     */
    public static String load(String stored) {
        if (stored == null || !stored.startsWith(PREFIX)) {
            return stored;
        }
        byte[] bytes = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
        char[] chars = new char[bytes.length / 2];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (((bytes[i * 2] & 0xFF) << 8) | (bytes[i * 2 + 1] & 0xFF));
        }
        return new String(chars);
    }
}
