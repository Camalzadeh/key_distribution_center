package bee01.humbat.keydistributioncenter.cryptography.keys;

import java.util.Random;

public class AsciiKey extends Key {

    private final String key;

    public AsciiKey(String key) {
        super(key);
        this.key = key;
    }

    public int getShift(int index) {
        return key.charAt(index % key.length()) % 128;
    }

    @Override
    public int size() {
        return key.length();
    }

    @Override
    public String getKey() {
        return key;
    }

    public static AsciiKey generateRandomKey(int length) {
        StringBuilder keyBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) random.nextInt(128);
            keyBuilder.append(randomChar);
        }
        return new AsciiKey(keyBuilder.toString());
    }
}
