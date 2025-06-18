package bee01.humbat.keydistributioncenter.cryptography.ciphers;

import bee01.humbat.keydistributioncenter.cryptography.keys.IntegerKey;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;
import bee01.humbat.keydistributioncenter.cryptography.interfaces.Blockable;

/**
 * {@code CaesarCipher} is a classical symmetric cipher based on monoalphabetic substitution.
 * <p>
 * Each character in the message is shifted by a fixed amount {@code n}, using modulo 128 arithmetic
 * to ensure the result remains within the ASCII range.
 * </p>
 * <p>
 * While simple, the Caesar cipher is historically important and illustrates key principles
 * such as symmetric encryption, modular operations, and brute-force attack vulnerability.
 * </p>
 */
public class CaesarCipher extends SymmetricCipher implements Blockable {

    public CaesarCipher(IntegerKey key) {
        super(key);
        this.algorithm = Algorithm.CAESAR;
    }
    public CaesarCipher(String key) {
        super(new IntegerKey(key));
        this.algorithm = Algorithm.CAESAR;
    }
    public CaesarCipher(int key) {
        super(new IntegerKey(key));
        this.algorithm = Algorithm.CAESAR;
    }
    public CaesarCipher() {
        super(IntegerKey.generateRandomKey(size));
        this.algorithm = Algorithm.CAESAR;
    }
    /**
     * Applies the Caesar shift to the input message.
     * <ul>
     *     <li>For encryption, each character is shifted forward by {@code n} (mod 128).</li>
     *     <li>For decryption, the character is shifted backward by {@code n} (mod 128).</li>
     * </ul>
     *
     * @param message the input message (plaintext or ciphertext)
     * @param encrypt {@code true} to encrypt, {@code false} to decrypt
     * @return the transformed message
     */
    @Override
    public String process(String message, boolean encrypt) {
        int n= (int)key.getKey();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char ch = process(message.charAt(i), encrypt);
            result.append(ch);
        }
        return result.toString();
    }

    public char process(char ch, boolean encrypt) {
        int n= (int)key.getKey();
        if (encrypt) {
            ch = (char) ((ch + n) % 128);
        } else {
            ch = (char) ((ch - n + 128) % 128);
        }
        return ch;
    }


    @Override
    public int getBlockSize() {
        return 1;
    }

    @Override
    public char encrypt(char ch, int position) {
        return process(ch, true);
    }

    @Override
    public char decrypt(char ch, int position) {
        return process(ch, false);
    }
}
