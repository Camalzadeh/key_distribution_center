package bee01.humbat.keydistributioncenter.cryptography.ciphers;

import bee01.humbat.keydistributioncenter.cryptography.keys.AsciiKey;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;
import bee01.humbat.keydistributioncenter.cryptography.interfaces.Blockable;

/**
 * {@code VigenereCipher} implements the classic Vigenère cipher as a symmetric encryption algorithm.
 * <p>
 * It uses a repeating key to apply character-wise modular shifts over the input message.
 * All operations are performed modulo 128 to keep the result within the standard ASCII range.
 * </p>
 * <p>
 * This cipher is vulnerable to frequency analysis if the key is short and the ciphertext is long,
 * but still serves as a fundamental example of polyalphabetic substitution.
 * </p>
 */
public class VigenereCipher extends SymmetricCipher implements Blockable {

    /**
     * Constructs a new {@code VigenereCipher} with the given key.
     *
     * @param key the shift pattern used for encryption and decryption
     */
    public VigenereCipher(AsciiKey key) {
        super(key);
        this.algorithm = Algorithm.VIGENERE;
    }

    public VigenereCipher(String key) {
        super(new AsciiKey(key));
        this.algorithm = Algorithm.VIGENERE;
    }

    public VigenereCipher() {
        super();
        this.algorithm = Algorithm.VIGENERE;
    }

    /**
     * Applies the Vigenère cipher transformation on a given message.
     * <p>
     * For encryption, each character is shifted forward using its corresponding key value.<br>
     * For decryption, the character is shifted backward by the same key value.
     * </p>
     * <p>
     * All transformations use modular arithmetic mod 128 to stay within ASCII bounds.
     * </p>
     *
     * @param message  the input plaintext or ciphertext
     * @param encrypt  {@code true} to encrypt, {@code false} to decrypt
     * @return the processed (encrypted or decrypted) message
     */
    @Override
    public String process(String message, boolean encrypt) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char ch = message.charAt(i);
            int shift = ((AsciiKey)key).getShift(i);

            char transformed = processChar(ch, shift, encrypt);

            result.append(transformed);
        }

        return result.toString();
    }

    private char processChar(char ch, int shift, boolean encrypt) {
        return encrypt
                ? (char) ((ch + shift) % 128)
                : (char) ((ch - shift + 128) % 128);
    }

    @Override
    public int getBlockSize() {
        return 1;
    }

    @Override
    public char encrypt(char ch, int position) {
        return processChar(ch, ((AsciiKey)key).getShift(position), true);
    }

    @Override
    public char decrypt(char ch, int position) {
        return processChar(ch, ((AsciiKey)key).getShift(position), false);
    }
}
