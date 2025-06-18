package bee01.humbat.keydistributioncenter.cryptography.keys;

import bee01.humbat.keydistributioncenter.cryptography.exceptions.NotBigIntegerException;

import java.math.BigInteger;

/**
 * Represents an RSA asymmetric key, storing the exponent and modulus as {@code BigInteger}.
 * This class is used for both public keys (e, n) and private keys (d, n) in RSA encryption.
 *
 * <p>
 * The exponent {@code e} can represent either:
 * <ul>
 *   <li>the public exponent (commonly 65537)</li>
 *   <li>or the private exponent derived as the modular inverse of e modulo φ(n)</li>
 * </ul>
 * The modulus {@code n = p × q} is a product of two large primes and defines the key's length and strength.
 * </p>
 */

public class AsymmetricKey extends Key {

    private final BigInteger e, n;

    /**
     * Constructs an {@code AsymmetricKey} using the given exponent and modulus.
     *
     * @param e the exponent (public or private)
     * @param n the RSA modulus
     */
    public AsymmetricKey(BigInteger e, BigInteger n) {
        super(toString(e, n));
        this.e = e;
        this.n = n;
    }

    /**
     * Parses an RSA key from a string of the format {@code "(e,n)"}.
     * Validates that both parts are proper {@code BigInteger} values.
     *
     * @param key the key string to parse
     * @throws NotBigIntegerException if the input is not a valid BigInteger pair
     */
    public AsymmetricKey(String key) {
        super(key);
        if (!isBigIntegerPair(key)) {
            throw new NotBigIntegerException("Key is not a valid BigInteger");
        }
        String[] parts = key.split(",");
        this.e = new BigInteger(parts[0].replace("(", ""));
        this.n = new BigInteger(parts[1].replace(")", ""));
    }

    /**
     * Constructs the key using string representations of exponent and modulus.
     *
     * @param e the exponent string
     * @param n the modulus string
     * @throws NotBigIntegerException if either string is not a valid BigInteger
     */
    public AsymmetricKey(String e, String n) {
        super("(" + e + "," + n + ")");
        if (isNotBigInteger(e) || isNotBigInteger(n)) {
            throw new NotBigIntegerException("Key is not a valid BigInteger");
        }
        this.e = new BigInteger(e);
        this.n = new BigInteger(n);
    }

    /**
     * Constructs the key using integer values (for testing or small examples).
     *
     * @param e the exponent
     * @param n the modulus
     */
    public AsymmetricKey(int e, int n) {
        super("(" + e + "," + n + ")");
        this.e = BigInteger.valueOf(e);
        this.n = BigInteger.valueOf(n);
    }

    /**
     * Converts the key into a canonical string format {@code "(e,n)"}.
     */
    public static String toString(BigInteger e, BigInteger n) {
        return "(" + e.toString() + "," + n.toString() + ")";
    }

    /**
     * Validates if the given string represents a valid RSA key in the format {@code "(e,n)"}.
     *
     * @param key the string to validate
     * @return {@code true} if both parts are valid BigIntegers, otherwise {@code false}
     */
    public static boolean isBigIntegerPair(String key) {
        String[] parts = key.split(",");
        if (parts.length != 2) {
            return false;
        }
        try {
            new BigInteger(parts[0].replace("(", ""));
            new BigInteger(parts[1].replace(")", ""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a given string is not a valid {@code BigInteger}.
     *
     * @param key the string to check
     * @return {@code true} if the string is invalid, otherwise {@code false}
     */
    public static boolean isNotBigInteger(String key) {
        try {
            new BigInteger(key);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * Returns the number of digits in the modulus {@code n}, representing key size.
     */
    @Override
    public int size() {
        return n.abs().toString().length();
    }

    /**
     * Returns the key as an array containing the exponent and modulus.
     * This is used in encryption and decryption routines.
     *
     * @return an array containing {@code [e, n]}
     */
    @Override
    public Object getKey() {
        return new BigInteger[]{e, n};
    }
}
