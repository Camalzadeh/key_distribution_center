package bee01.humbat.keydistributioncenter.cryptography.keys;

import bee01.humbat.keydistributioncenter.cryptography.exceptions.NotIntegerKeyException;

import java.util.Random;

public class IntegerKey extends Key {
    private final int key;

    public IntegerKey(String key) {
        super(key);
        if (!isInteger(key)) {
            throw new NotIntegerKeyException("Key must be an integer");
        }

        this.key = Integer.parseInt(key);
    }
    public IntegerKey(int key) {
        super(String.valueOf(key));
        this.key = key;
    }

    public static boolean isInteger(String key) {
        try{
            Integer.parseInt(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static IntegerKey generateRandomKey(int bound) {
        Random random = new Random();
        int randomKey = random.nextInt(bound);
        return new IntegerKey(randomKey);
    }

    @Override
    public int size() {
        return key;
    }
    @Override
    public Object getKey() {
        return key;
    }
}
