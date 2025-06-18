package bee01.humbat.keydistributioncenter.cryptography.enums;

import bee01.humbat.keydistributioncenter.cryptography.interfaces.Blockable;

public enum Mode {
    ECB {
        @Override
        public char process(char ch, char block, boolean encrypting, Blockable cipher, int position) {
            return encrypting
                    ? cipher.encrypt(ch, position)
                    : cipher.decrypt(ch, position);
        }
    },
    CBC {
        @Override
        public char process(char ch, char block, boolean encrypting, Blockable cipher, int position) {
            if (encrypting) {
                char xored = (char) (ch ^ block);
                return cipher.encrypt(xored, position);
            } else {
                char decrypted = cipher.decrypt(ch, position);
                return (char) (decrypted ^ block);
            }
        }
    },
    CFB {
        @Override
        public char process(char ch, char block, boolean encrypting, Blockable cipher, int position) {
            char output = cipher.encrypt(block, position);
            char result = (char) (output ^ ch);
            return result;
        }
    },
    OFB {
        @Override
        public char process(char ch, char block, boolean encrypting, Blockable cipher, int position) {
            char output = cipher.encrypt(block, position);
            char result = (char) (output ^ ch);
            return result;
        }
    },
    NONE {
        @Override
        public char process(char ch, char block, boolean encrypting, Blockable cipher, int position) {
            return ch;
        }
    };

    public  static Mode getMode(String mode){
        for(Mode m : Mode.values()){
            if(m.name().equalsIgnoreCase(mode)){
                return m;
            }
        }
        throw new UnsupportedOperationException("Mode not supported: " + mode);
    }

    public abstract char process(char ch, char block, boolean encrypting, Blockable cipher, int position);
}
