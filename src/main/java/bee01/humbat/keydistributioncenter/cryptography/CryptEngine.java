package bee01.humbat.keydistributioncenter.cryptography;

import bee01.humbat.keydistributioncenter.cryptography.interfaces.Blockable;
import bee01.humbat.keydistributioncenter.cryptography.interfaces.Cryptable;
import bee01.humbat.keydistributioncenter.cryptography.ciphers.SymmetricCipher;
import bee01.humbat.keydistributioncenter.cryptography.enums.Mode;
import bee01.humbat.keydistributioncenter.cryptography.pojos.ConfigPojo;
import bee01.humbat.keydistributioncenter.cryptography.pojos.ModePojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@code CryptEngine} is a modular cryptographic engine designed for
 * character-based block cipher operations.
 * <p>
 * It supports pluggable cipher algorithms (e.g., Vigenère) and applies
 * them in standard block cipher modes like ECB (Electronic Codebook) and
 * CBC (Cipher Block Chaining).
 * </p>
 *
 * @param <T> A cipher algorithm that implements the {@link Cryptable} interface.
 */
public class CryptEngine<T extends Cryptable> {

    /**
     * The concrete cipher algorithm used for per-character encryption/decryption.
     * This must implement the {@link Cryptable} interface.
     */
    private final T cipher;

    /**
     * The block cipher mode of operation:
     * <ul>
     *   <li><b>ECB</b> — Each character is encrypted independently.</li>
     *   <li><b>CBC</b> — Each character is XORed with the previous cipher block,
     *   forming a dependency chain between blocks.</li>
     * </ul>
     */
    private Mode mode = Mode.NONE;

    /**
     * Initialization vector used for CBC mode.
     * Used only for the first block to provide randomness or fixed chaining.
     */
    private char iv = 0x00;

    /**
     * Constructs a {@code CryptEngine} with the given cipher algorithm and mode.
     *
     * @param cipher the encryption algorithm to use (e.g., VigenereCipher)
     * @param mode   the block cipher mode (ECB or CBC)
     */
    public CryptEngine(T cipher, Mode mode) {
        this.cipher = cipher;
        this.mode = mode;
    }

    public CryptEngine(T cipher) {
        this.cipher = cipher;
    }

    public CryptEngine(T cipher, Mode mode, char iv) {
        this.cipher = cipher;
        this.mode = mode;
        this.iv = iv;
    }

    @SuppressWarnings("unchecked")
    public CryptEngine(ConfigPojo pojo) {
        this.cipher = (T) pojo.key().algorithm().getAlgorithmClass(pojo.key().key());
        this.mode = pojo.mode().mode();
        this.iv = pojo.mode().iv();
    }


    /**
     * Encrypts the input message using the configured cipher and block mode.
     *
     * @param message the plaintext input
     * @return the encrypted ciphertext
     */
    public String encrypt(String message) {
        return process(message, true);
    }

    /**
     * Decrypts the input message using the configured cipher and block mode.
     *
     * @param message the ciphertext input
     * @return the decrypted plaintext
     */
    public String decrypt(String message) {
        return process(message, false);
    }

    /**
     * Core cryptographic processing logic that applies either ECB or CBC mode
     * using the configured {@link Cryptable} cipher.
     * <p>
     * - In <b>ECB</b>, each character is encrypted independently.<br>
     * - In <b>CBC</b>, each character is XORed with the previous cipher block
     * (or IV for the first block) before encryption; during decryption, the reverse
     * XOR is applied after decrypting.
     * </p>
     *
     * @param message    the input message
     * @param encrypting true to encrypt, false to decrypt
     * @return the processed output message
     */

    private String process(String message, boolean encrypting) {
        if (cipher instanceof Blockable && hasMode()) {
            return processBlocks(message, encrypting);
        }
        return processFull(message, encrypting);

    }

    private String processBlocks(String message, boolean encrypting) {
        StringBuilder result = new StringBuilder();
        char block = iv;

        for (int i = 0; i < message.length(); i++) {
            char processed = mode.process(message.charAt(i), block, encrypting, (Blockable) cipher, i);

            switch (mode) {
                case CBC, CFB -> block = encrypting ? processed : message.charAt(i);
                case OFB -> block = ((Blockable) cipher).encrypt(block, i);
            }

            result.append(processed);
        }


        return result.toString();
    }

    private String processFull(String message, boolean encrypting) {
        return encrypting ? cipher.encrypt(message) : cipher.decrypt(message);
    }

    /**
     * Encrypts the contents of a plaintext file and writes the encrypted output
     * to a separate file, along with the key and mode used (if the cipher is symmetric).
     *
     * @param inputPath the path to the input plaintext file
     * @throws IOException if reading or writing fails
     */
    public void encryptFile(Path inputPath) throws IOException {
        String content = Files.readString(inputPath);
        String encrypted = encrypt(content);

        Path dir = inputPath.getParent();
        Files.writeString(dir.resolve("encrypted_message.txt"), encrypted);

        ConfigPojo pojo = new ConfigPojo(
                cipher.getPojo(),
                new ModePojo(mode, iv)
        );
        pojo.writeToFile(dir.resolve("config.txt"));
    }

    boolean hasMode() {
        return mode != Mode.NONE && mode != null;
    }

    /**
     * Decrypts the contents of an encrypted file using the previously stored
     * key and mode. This method assumes that the correct cipher and configuration
     * are already in place.
     *
     * @param encryptedFilePath the path to the encrypted message file
     * @throws IOException if reading or writing fails
     */
    public void decryptFile(Path encryptedFilePath) throws IOException {

        String encryptedContent = Files.readString(encryptedFilePath);
        String decrypted = decrypt(encryptedContent);

        Path dir = encryptedFilePath.getParent();
        Files.writeString(dir.resolve("decrypted_message.txt"), decrypted);
    }

    public static void decryptFile(Path encryptedFilePath, ConfigPojo pojo) throws IOException {
        String encryptedContent = Files.readString(encryptedFilePath);
        CryptEngine<SymmetricCipher> engine = new CryptEngine<>(pojo);
        String decrypted = engine.decrypt(encryptedContent);

        Path dir = encryptedFilePath.getParent();
        Files.writeString(dir.resolve("decrypted_message.txt"), decrypted);
    }
}
