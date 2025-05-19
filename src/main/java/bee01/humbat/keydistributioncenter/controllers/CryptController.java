package bee01.humbat.keydistributioncenter.controllers;

import bee01.humbat.keydistributioncenter.cryptography.CryptEngine;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;
import bee01.humbat.keydistributioncenter.cryptography.enums.Mode;
import bee01.humbat.keydistributioncenter.cryptography.keys.Key;
import bee01.humbat.keydistributioncenter.cryptography.pojos.ConfigPojo;
import bee01.humbat.keydistributioncenter.cryptography.pojos.KeyPojo;
import bee01.humbat.keydistributioncenter.cryptography.pojos.ModePojo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class CryptController {

    @GetMapping("/encrypt")
    public String encryptPage() {
        return "encrypt";
    }

    @PostMapping("/encrypt/process")
    public String encryptProcess(@RequestParam String algorithm,
                                 @RequestParam Optional<String> mode,
                                 @RequestParam String key,
                                 @RequestParam String text,
                                 Model model) {

        try {
            Algorithm algoEnum = Algorithm.getAlgorithm(algorithm);
            Mode modeEnum = Mode.getMode(mode.orElse("NONE"));
            Key parsedKey = Key.giveKey(key, algoEnum);

            ConfigPojo config = new ConfigPojo(new KeyPojo(algoEnum, parsedKey), new ModePojo(modeEnum));
            CryptEngine<?> engine = new CryptEngine<>(config);
            String encryptedText = engine.encrypt(text);

            model.addAttribute("encryptedText", encryptedText);
        } catch (Exception e) {
            model.addAttribute("error", "Encryption failed: " + e.getMessage());
        }

        return "encrypt";
    }

    @GetMapping("/decrypt")
    public String decryptPage() {
        return "decrypt";
    }

    @PostMapping("/decrypt/process")
    public String decryptProcess(@RequestParam String algorithm,
                                 @RequestParam Optional<String> mode,
                                 @RequestParam String key,
                                 @RequestParam String encryptedText,
                                 Model model) {

        try {
            Algorithm algoEnum = Algorithm.getAlgorithm(algorithm);
            Mode modeEnum = Mode.getMode(mode.orElse("NONE"));
            Key parsedKey = Key.giveKey(key, algoEnum);

            ConfigPojo config = new ConfigPojo(new KeyPojo(algoEnum, parsedKey), new ModePojo(modeEnum));
            CryptEngine<?> engine = new CryptEngine<>(config);
            String decryptedText = engine.decrypt(encryptedText);

            model.addAttribute("decryptedText", decryptedText);
        } catch (Exception e) {
            model.addAttribute("error", "Decryption failed: " + e.getMessage());
        }

        return "decrypt";
    }
}
