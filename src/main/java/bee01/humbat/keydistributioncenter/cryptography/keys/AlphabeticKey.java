package bee01.humbat.keydistributioncenter.cryptography.keys;

import bee01.humbat.keydistributioncenter.cryptography.exceptions.NotAlphabeticKeyException;

import java.util.Random;

public class AlphabeticKey extends Key {
    private final String key;

    public AlphabeticKey(String key) {
        super(key);
        if (!isAlphabetic(key)) {
            throw new NotAlphabeticKeyException("Key must be alphabetic");
        }
        this.key = key;
    }

    public static boolean isAlphabetic(String key) {
        return key.matches("[a-zA-Z]+");
    }

    public static AlphabeticKey generateRandomKey(int length) {
        StringBuilder keyBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('a' + random.nextInt(26));
            keyBuilder.append(randomChar);
        }
        return new AlphabeticKey(keyBuilder.toString());
    }

    @Override
    public int size() {
        return key.length();
    }

    @Override
    public Object getKey() {
        return key;
    }
}
