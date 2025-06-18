package bee01.humbat.keydistributioncenter.cryptography.pojos;

import bee01.humbat.keydistributioncenter.cryptography.enums.Mode;

public record ModePojo(Mode mode, char iv) {
    public ModePojo(Mode mode) {
        this(mode, (char)0x00);
    }

    public ModePojo(Mode mode, char iv) {
        this.mode = mode;
        this.iv = iv;
    }

    @Override
    public String toString() {
        return "Mode: " + mode + ", IV: " + iv;
    }
}
