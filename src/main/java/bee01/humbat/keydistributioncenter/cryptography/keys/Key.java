package bee01.humbat.keydistributioncenter.cryptography.keys;


import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;

public abstract class Key {
    protected final String rawKey;

    public Key(String key) {
        this.rawKey = key;
    }

    public static Key generateRandomKey(int length) {
        return AsciiKey.generateRandomKey(length);
    }
    public static Key giveKey(String keyValue) {
        if(IntegerKey.isInteger(keyValue)){
            return new IntegerKey(keyValue);
        }else if(AlphabeticKey.isAlphabetic(keyValue)) {
            return new AlphabeticKey(keyValue);
        }else if(AsymmetricKey.isBigIntegerPair(keyValue)) {
            return new AsymmetricKey(keyValue);
        }
        return new AsciiKey(keyValue);
    }

    public static Key giveKey(String keyValue, Algorithm algorithm) {
        if(algorithm == Algorithm.RSA) {
            return new AsymmetricKey(keyValue);
        } else if(algorithm == Algorithm.CAESAR || algorithm == Algorithm.RAILFENCE) {
            return new IntegerKey(keyValue);
        } else if(algorithm == Algorithm.PLAYFAIR) {
            return new AlphabeticKey(keyValue);
        }
        return new AsciiKey(keyValue);
    }

    public abstract int size();
    public abstract Object getKey();


    @Override
    public String toString() {
        return rawKey;
    }
}
