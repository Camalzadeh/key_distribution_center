package bee01.humbat.keydistributioncenter.cryptography.interfaces;

import bee01.humbat.keydistributioncenter.cryptography.pojos.KeyPojo;

/**
 * {@code Cryptable} defines a generic interface for cryptographic algorithms
 * capable of encrypting and decrypting character-based messages.
 * <p>
 * Implementations of this interface must provide symmetric or asymmetric logic
 * to transform a plaintext message into ciphertext and vice versa.
 * </p>
 */
public interface Cryptable {

    /**
     * Encrypts the given plaintext message using a specific algorithm and key.
     *
     * @param message the plaintext input
     * @return the encrypted ciphertext
     */
    String encrypt(String message);

    /**
     * Decrypts the given ciphertext message back into its original plaintext form.
     *
     * @param message the encrypted input
     * @return the decrypted plaintext
     */
    String decrypt(String message);

    KeyPojo getPojo();
}
