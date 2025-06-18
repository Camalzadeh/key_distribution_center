package bee01.humbat.keydistributioncenter.cryptography.enums;

import bee01.humbat.keydistributioncenter.cryptography.ciphers.*;
import bee01.humbat.keydistributioncenter.cryptography.interfaces.Cryptable;
import bee01.humbat.keydistributioncenter.cryptography.keys.Key;

public enum Algorithm {
    VIGENERE,
    CAESAR,
    PLAYFAIR,
    RAILFENCE,
    RSA;

    public Cryptable getAlgorithmClass (Key key) {
        return switch (this) {
            case VIGENERE -> new VigenereCipher((String)key.getKey());
            case CAESAR -> new CaesarCipher((Integer) key.getKey());
            case PLAYFAIR -> new PlayFairCipher((String)key.getKey());
            case RAILFENCE -> new RailFenceCipher((Integer)key.getKey());
            case RSA -> new RsaCipher((String)key.getKey());
            default -> throw new UnsupportedOperationException("Algorithm not supported: " + this);
        };
    }
    public static Algorithm getAlgorithm(String algorithm) {
        for(Algorithm algo : Algorithm.values()) {
            if(algo.name().equalsIgnoreCase(algorithm)) {
                return algo;
            }
        }
        throw new UnsupportedOperationException("Algorithm not supported: " + algorithm);
    }
}
