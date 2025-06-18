package bee01.humbat.keydistributioncenter.cryptography;

import bee01.humbat.keydistributioncenter.cryptography.keys.AsymmetricKey;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Responsible for generating RSA key pairs for asymmetric cryptographic communication.
 * This class simulates the role of a Key Distribution Center (KDC) in public key infrastructures.
 *
 * <p>
 * The key generation process includes:
 * <ul>
 *   <li>Generating two large prime numbers {@code p} and {@code q}</li>
 *   <li>Computing the RSA modulus {@code n = p * q}</li>
 *   <li>Calculating Euler's totient function {@code φ(n) = (p - 1)(q - 1)}</li>
 *   <li>Selecting a public exponent {@code e} that is coprime with φ(n)</li>
 *   <li>Computing the private exponent {@code d}, the modular inverse of {@code e} modulo φ(n)</li>
 * </ul>
 * </p>
 */
public class KeyDistributionCenter {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a cryptographically valid RSA key pair with a specified modulus size.
     *
     * @param bitLength the total bit length of the RSA modulus {@code n}.
     *                  For practical security, values like 1024, 2048, or 4096 are recommended.
     * @return an array of two {@link AsymmetricKey} objects:
     *         <ul>
     *           <li>Index 0 — the public key {@code (e, n)}</li>
     *           <li>Index 1 — the private key {@code (d, n)}</li>
     *         </ul>
     */
    public static AsymmetricKey[] generateKeyPair(int bitLength) {
        BigInteger p = BigInteger.probablePrime(bitLength / 2, RANDOM);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, RANDOM);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        BigInteger e;
        do {
            e = BigInteger.probablePrime(17, RANDOM);
        } while (!phi.gcd(e).equals(BigInteger.ONE) || e.compareTo(phi) >= 0);

        BigInteger d = e.modInverse(phi);

        AsymmetricKey publicKey = new AsymmetricKey(e, n);
        AsymmetricKey privateKey = new AsymmetricKey(d, n);

        return new AsymmetricKey[]{publicKey, privateKey};
    }

}
