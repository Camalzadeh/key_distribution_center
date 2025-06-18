package bee01.humbat.keydistributioncenter.cryptography.ciphers;

import bee01.humbat.keydistributioncenter.cryptography.keys.AlphabeticKey;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;

/**
 * PlayFairCipher implements the classical Playfair cipher encryption algorithm.
 * This is a symmetric encryption technique based on digraph substitution using a 5x5 matrix.
 *
 * <p>In this implementation, the key is used to initialize a matrix of unique letters (A–Z, with 'J' replaced by 'I').
 * The plaintext is split into digraphs, and each pair is encrypted using Playfair rules:
 * - Same row → shift right
 * - Same column → shift down
 * - Rectangle → swap columns
 *
 * <p>This cipher ignores non-alphabetic characters and converts all letters to uppercase.
 */
public class PlayFairCipher extends SymmetricCipher {

    /** 5x5 matrix used for encryption and decryption, initialized from the key */
    private final char[][] matrix = new char[5][5];

    /**
     * Constructs a PlayFairCipher with a specified alphabetic key.
     * The key defines the initial layout of the 5x5 matrix used in encryption.
     *
     * @param key The cryptographic key used to generate the matrix (letters only, 'J' is excluded)
     */
    public PlayFairCipher(AlphabeticKey key) {
        super(key);
        this.algorithm = Algorithm.PLAYFAIR;
        String matrixKey=(String) key.getKey();
        initMatrix(matrixKey.toUpperCase());
    }

    /**
     * Constructs a PlayFairCipher with a specified key string.
     * The key string is converted to an AlphabeticKey for matrix initialization.
     *
     * @param key The key string used to generate the matrix (letters only, 'J' is excluded)
     */
    public PlayFairCipher(String key) {
        super(new AlphabeticKey(key));
        this.algorithm = Algorithm.PLAYFAIR;
        initMatrix(key.toUpperCase());
    }

    /**
     * Constructs a PlayFairCipher with a randomly generated key.
     * The matrix is initialized using a randomly created alphabetic key.
     */
    public PlayFairCipher() {
        super(AlphabeticKey.generateRandomKey(size));
        this.algorithm = Algorithm.PLAYFAIR;
        initMatrix(((String) key.getKey()).toUpperCase());
    }

    /**
     * Initializes the Playfair matrix using the provided key.
     * The matrix contains unique uppercase letters A–Z, skipping 'J'.
     *
     * @param key The uppercase key string used to fill the matrix
     */
    void initMatrix(String key) {
        StringBuilder uniqueChars = new StringBuilder();
        for (char ch : key.toCharArray()) {
            if (uniqueChars.indexOf(String.valueOf(ch)) == -1 && ch != 'J') {
                uniqueChars.append(ch);
            }
        }
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            if (uniqueChars.indexOf(String.valueOf(ch)) == -1 && ch != 'J') {
                uniqueChars.append(ch);
            }
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = uniqueChars.charAt(i * 5 + j);
            }
        }
    }

    /**
     * Encrypts or decrypts a message using the Playfair cipher.
     * The message is preprocessed and converted into digraphs.
     *
     * @param message The input plaintext or ciphertext
     * @param encrypt If true, performs encryption; otherwise, performs decryption
     * @return The resulting encrypted or decrypted message
     */
    @Override
    public String process(String message, boolean encrypt) {
        message = preprocess(message);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i += 2) {
            char a = message.charAt(i);
            char b = message.charAt(i + 1);
            result.append(encrypt ? encryptPair(a, b) : decryptPair(a, b));
        }
        return result.toString();
    }

    /**
     * Prepares the plaintext for encryption by:
     * - Converting to uppercase
     * - Removing non-alphabetic characters
     * - Replacing 'J' with 'I'
     * - Splitting into digraphs (pairs)
     * - Inserting 'X' between identical letters
     * - Padding with 'X' if length is odd
     *
     * @param message The original input message
     * @return A normalized string ready for encryption
     */
    private String preprocess(String message) {
        message = message.toUpperCase().replaceAll("[^A-Z]", "").replace('J', 'I');
        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < message.length()) {
            char a = message.charAt(i++);
            char b;

            if (i < message.length()) {
                b = message.charAt(i);
                if (a == b) {
                    b = 'X';
                } else {
                    i++;
                }
            } else {
                b = 'X';
            }

            sb.append(a).append(b);
        }
        return sb.toString();
    }

    /**
     * Encrypts a pair of characters using Playfair cipher rules.
     * Applies row, column, or rectangle transformation based on positions in the matrix.
     *
     * @param a The first character of the pair
     * @param b The second character of the pair
     * @return The encrypted pair of characters
     */
    private String encryptPair(char a, char b) {
        int[] posA = findPosition(a);
        int[] posB = findPosition(b);
        assert posA != null;
        assert posB != null;
        if (posA[0] == posB[0]) {
            return "" + matrix[posA[0]][(posA[1] + 1) % 5] + matrix[posB[0]][(posB[1] + 1) % 5];
        } else if (posA[1] == posB[1]) {
            return "" + matrix[(posA[0] + 1) % 5][posA[1]] + matrix[(posB[0] + 1) % 5][posB[1]];
        } else {
            return "" + matrix[posA[0]][posB[1]] + matrix[posB[0]][posA[1]];
        }
    }

    /**
     * Decrypts a pair of characters using Playfair cipher rules.
     * Applies reverse row, column, or rectangle transformation.
     *
     * @param a The first encrypted character
     * @param b The second encrypted character
     * @return The decrypted pair of characters
     */
    private String decryptPair(char a, char b) {
        int[] posA = findPosition(a);
        int[] posB = findPosition(b);
        assert posA != null;
        assert posB != null;
        if (posA[0] == posB[0]) {
            return "" + matrix[posA[0]][(posA[1] + 4) % 5] + matrix[posB[0]][(posB[1] + 4) % 5];
        } else if (posA[1] == posB[1]) {
            return "" + matrix[(posA[0] + 4) % 5][posA[1]] + matrix[(posB[0] + 4) % 5][posB[1]];
        } else {
            return "" + matrix[posA[0]][posB[1]] + matrix[posB[0]][posA[1]];
        }
    }

    /**
     * Finds the row and column indices of a character in the 5x5 Playfair matrix.
     *
     * @param ch The character to locate
     * @return An array {row, column} indicating the position in the matrix
     */
    private int[] findPosition(char ch) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == ch) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
