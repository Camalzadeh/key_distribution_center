package bee01.humbat.keydistributioncenter.cryptography.ciphers;

import bee01.humbat.keydistributioncenter.cryptography.keys.IntegerKey;
import bee01.humbat.keydistributioncenter.cryptography.keys.Key;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;

public class RailFenceCipher extends SymmetricCipher {

    public RailFenceCipher(IntegerKey key) {
        super(key);
        this.algorithm = Algorithm.RAILFENCE;
    }
    public RailFenceCipher(String key) {
        super(new IntegerKey(key));
        this.algorithm = Algorithm.RAILFENCE;
    }
    public RailFenceCipher(int key) {
        super(new IntegerKey(key));
        this.algorithm = Algorithm.RAILFENCE;
    }
    public RailFenceCipher() {
        super(IntegerKey.generateRandomKey(size));
        this.algorithm = Algorithm.RAILFENCE;
    }
    @Override
    public String process(String message, boolean encrypt) {
        int rails = (int)key.getKey();
        if (rails <= 1 || message.isEmpty()) return message;

        char[] chars = message.toCharArray();
        int len = chars.length;

        // Create rows
        StringBuilder[] fence = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) fence[i] = new StringBuilder();

        if (encrypt) {
            int row = 0;
            boolean down = true;

            for (char c : chars) {
                fence[row].append(c);
                if (row == 0) down = true;
                else if (row == rails - 1) down = false;
                row += down ? 1 : -1;
            }

            // Join all rows
            StringBuilder result = new StringBuilder();
            for (StringBuilder sb : fence) result.append(sb);
            return result.toString();
        } else {
            // Create pattern map
            int[] pos = new int[len];
            int row = 0;
            boolean down = true;
            for (int i = 0; i < len; i++) {
                pos[i] = row;
                if (row == 0) down = true;
                else if (row == rails - 1) down = false;
                row += down ? 1 : -1;
            }

            // Count occurrences in each row
            int[] counts = new int[rails];
            for (int p : pos) counts[p]++;

            // Assign characters to each row
            char[][] rowChars = new char[rails][];
            int idx = 0;
            for (int i = 0; i < rails; i++) {
                rowChars[i] = message.substring(idx, idx + counts[i]).toCharArray();
                idx += counts[i];
            }

            // Reconstruct message
            int[] rowIdx = new int[rails];
            StringBuilder result = new StringBuilder();
            for (int p : pos) {
                result.append(rowChars[p][rowIdx[p]++]);
            }
            return result.toString();
        }
    }


}
