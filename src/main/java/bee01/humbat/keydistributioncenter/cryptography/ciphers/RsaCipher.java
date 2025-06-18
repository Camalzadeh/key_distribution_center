package bee01.humbat.keydistributioncenter.cryptography.ciphers;

import bee01.humbat.keydistributioncenter.cryptography.KeyDistributionCenter;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;
import bee01.humbat.keydistributioncenter.cryptography.interfaces.Cryptable;
import bee01.humbat.keydistributioncenter.cryptography.keys.AsymmetricKey;
import bee01.humbat.keydistributioncenter.cryptography.pojos.KeyPojo;

import java.math.BigInteger;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the RSA public-key encryption algorithm.
 * This cipher performs modular exponentiation using a given RSA key,
 * which can be either a public key {@code (e, n)} or a private key {@code (d, n)}.
 *
 * <p>
 * The encryption and decryption process are based on:
 * <ul>
 *   <li>Encryption: {@code c = m^e mod n}</li>
 *   <li>Decryption: {@code m = c^d mod n}</li>
 * </ul>
 * where {@code m} is the plaintext message as a number, and {@code c} is the ciphertext.
 * </p>
 */
public class RsaCipher implements Cryptable {

    private final AsymmetricKey key;
    private final Algorithm algorithm = Algorithm.RSA;

    /**
     * Constructs an RSA cipher using exponent and modulus as {@code BigInteger}.
     *
     * @param e the exponent (public or private)
     * @param n the modulus
     */
    public RsaCipher(BigInteger e, BigInteger n) {
        this.key = new AsymmetricKey(e, n);
    }

    /**
     * Constructs an RSA cipher from exponent and modulus strings.
     *
     * @param e the exponent as string
     * @param n the modulus as string
     */
    public RsaCipher(String e, String n) {
        this.key = new AsymmetricKey(e, n);
    }

    /**
     * Constructs an RSA cipher from integer exponent and modulus (for simple/testing use).
     *
     * @param e the exponent
     * @param n the modulus
     */
    public RsaCipher(int e, int n) {
        this.key = new AsymmetricKey(e, n);
    }

    /**
     * Constructs an RSA cipher by parsing a serialized key string of the form {@code "(e,n)"}.
     *
     * @param key the serialized key string
     */
    public RsaCipher(String key) {
        this.key = new AsymmetricKey(key);
    }

    /**
     * Constructs an RSA cipher using an existing {@link AsymmetricKey} object.
     *
     * @param key the RSA key (public or private)
     */
    public RsaCipher(AsymmetricKey key) {
        this.key = key;
    }

    /**
     * Encrypts a plaintext message using the RSA public key {@code (e, n)}.
     * <p>
     * The message is converted to a {@code BigInteger} and raised to the power of {@code e} modulo {@code n}.
     * The resulting ciphertext is returned in Base64-encoded form.
     * </p>
     *
     * @param message the plaintext message to encrypt
     * @return the Base64-encoded ciphertext
     */
    @Override
    public String encrypt(String message) {
        BigInteger[] publicKey = (BigInteger[]) key.getKey();
        BigInteger e = publicKey[0];
        BigInteger n = publicKey[1];

        BigInteger m = new BigInteger(1, message.getBytes());
        BigInteger c = m.modPow(e, n);

        return Base64.getEncoder().encodeToString(c.toByteArray());
    }

    public static List<AsymmetricKey[]> generateKeyPairs(){
        List<AsymmetricKey[]> keys = new ArrayList<>();
        for(int i = 0; i < 15; i++){
            keys.add(KeyDistributionCenter.generateKeyPair(1024));
        }
        return keys;
    }

    public static String encrypt(String message, AsymmetricKey key) {
        BigInteger[] publicKey = (BigInteger[]) key.getKey();
        BigInteger e = publicKey[0];
        BigInteger n = publicKey[1];

        BigInteger m = new BigInteger(1, message.getBytes());
        BigInteger c = m.modPow(e, n);

        return Base64.getEncoder().encodeToString(c.toByteArray());
    }

    public static String decrypt(String message, AsymmetricKey key) {
        BigInteger[] privateKey = (BigInteger[]) key.getKey();
        BigInteger d = privateKey[0];
        BigInteger n = privateKey[1];

        byte[] ciphertextBytes = Base64.getDecoder().decode(message);
        BigInteger c = new BigInteger(1, ciphertextBytes);
        BigInteger m = c.modPow(d, n);

        return new String(m.toByteArray());
    }


    /**
     * Decrypts a Base64-encoded ciphertext using the RSA private key {@code (d, n)}.
     * <p>
     * The ciphertext is first Base64-decoded and converted to a {@code BigInteger},
     * then decrypted via modular exponentiation with the private exponent.
     * </p>
     *
     * @param message the Base64-encoded ciphertext
     * @return the decrypted plaintext string
     */
    @Override
    public String decrypt(String message) {
        BigInteger[] privateKey = (BigInteger[]) key.getKey();
        BigInteger d = privateKey[0];
        BigInteger n = privateKey[1];

        byte[] ciphertextBytes = Base64.getDecoder().decode(message);
        BigInteger c = new BigInteger(1, ciphertextBytes);
        BigInteger m = c.modPow(d, n);

        return new String(m.toByteArray());
    }

    /**
     * Returns a serializable key object containing algorithm type and key.
     *
     * @return a {@link KeyPojo} representing the RSA key and algorithm
     */
    @Override
    public KeyPojo getPojo() {
        return new KeyPojo(algorithm, key);
    }
}
