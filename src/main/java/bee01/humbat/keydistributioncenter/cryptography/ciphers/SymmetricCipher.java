package bee01.humbat.keydistributioncenter.cryptography.ciphers;

import bee01.humbat.keydistributioncenter.cryptography.keys.Key;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;
import bee01.humbat.keydistributioncenter.cryptography.interfaces.Cryptable;
import bee01.humbat.keydistributioncenter.cryptography.pojos.KeyPojo;

/**
 * {@code SymmetricCipher} is an abstract base class for symmetric encryption algorithms.
 * <p>
 * Symmetric ciphers use the same key for both encryption and decryption.
 * This class standardizes the method structure for all implementing algorithms
 * and delegates core logic to a single abstract {@code process} method.
 * </p>
 * <p>
 * All derived classes must implement the specific transformation logic and expose their key.
 * </p>
 */
public abstract class SymmetricCipher implements Cryptable {

    /**
     * The secret key used to compute the shift for each character in the message.
     * The shift value at position {@code i} is determined by {@code key.getShift(i)}.
     */
    protected final Key key;
    protected Algorithm algorithm;
    protected static int size=10;


    protected SymmetricCipher(Key key) {
        this.key = key;
    }
    protected SymmetricCipher() { this.key = Key.generateRandomKey(size); }

    /**
     * Encrypts the given plaintext message by invoking {@link #process(String, boolean)}
     * with the {@code encrypt} flag set to {@code true}.
     *
     * @param message the plaintext input
     * @return the encrypted ciphertext
     */
    @Override
    public String encrypt(String message) {
        return process(message, true);
    }

    /**
     * Decrypts the given ciphertext message by invoking {@link #process(String, boolean)}
     * with the {@code encrypt} flag set to {@code false}.
     *
     * @param message the encrypted input
     * @return the decrypted plaintext
     */
    @Override
    public String decrypt(String message) {
        return process(message, false);
    }

    /**
     * Core transformation logic for the cipher.
     * <p>
     * This method must be implemented by concrete cipher classes to define
     * how characters are shifted, substituted, or otherwise transformed
     * during encryption and decryption.
     * </p>
     *
     * @param message  the input message (plaintext or ciphertext)
     * @param encrypt  {@code true} for encryption, {@code false} for decryption
     * @return the transformed output message
     */
    public abstract String process(String message, boolean encrypt);

    /**
     * Returns the key used by the cipher.
     * <p>
     * This key may be used for secure storage, transmission, or reconstruction
     * of the cipher state.
     * </p>
     *
     * @return the {@link Key} associated with the cipher instance
     */
    @Override
    public KeyPojo getPojo(){
        return new KeyPojo(algorithm,key);
    };
}
