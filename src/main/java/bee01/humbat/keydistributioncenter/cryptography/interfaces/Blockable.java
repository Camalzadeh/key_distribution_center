package bee01.humbat.keydistributioncenter.cryptography.interfaces;

public interface Blockable {
    int getBlockSize();
    char encrypt(char ch, int position);
    char decrypt(char ch, int position);
}
