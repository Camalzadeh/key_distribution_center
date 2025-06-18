package bee01.humbat.keydistributioncenter.cryptography.pojos;

import bee01.humbat.keydistributioncenter.cryptography.keys.Key;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;
import bee01.humbat.keydistributioncenter.cryptography.enums.Mode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;



public record ConfigPojo(KeyPojo key, ModePojo mode) {
    public void writeToFile(Path path) throws IOException {
        String sb = "key=" + key.key().toString() + "\n" +
                "algorithm=" + key.algorithm().name() + "\n" +
                "mode=" + mode.mode().name() + "\n" +
                "iv=" + mode.iv() + "\n";
        Files.writeString(path, sb);
    }

    public static ConfigPojo fromFile(Path path) throws IOException {
        String content = Files.readString(path);
        String[] lines = content.split("\\R");

        String keyValue = null;
        String algorithmValue = null;
        String modeValue = null;
        String ivValue = null;


        for (String line : lines) {
            if (line.startsWith("key=")) {
                keyValue = line.substring(4);
            } else if (line.startsWith("mode=")) {
                modeValue = line.substring(5);
            }
            else if (line.startsWith("algorithm=")) {
                algorithmValue = line.substring(10);
            } else if (line.startsWith("iv=")) {
                ivValue = line.substring(3);
            }
        }

        if (keyValue == null || modeValue == null || algorithmValue == null || ivValue == null) {
            throw new IllegalArgumentException("Invalid file format.");
        }

        return new ConfigPojo(
                new KeyPojo(Algorithm.valueOf(algorithmValue), Key.giveKey(keyValue)),
                new ModePojo(Mode.valueOf(modeValue), ivValue.charAt(0))
        );
    }
}
