package bee01.humbat.keydistributioncenter.services;

import bee01.humbat.keydistributioncenter.cryptography.CryptEngine;
import bee01.humbat.keydistributioncenter.cryptography.ciphers.RsaCipher;
import bee01.humbat.keydistributioncenter.cryptography.enums.Algorithm;
import bee01.humbat.keydistributioncenter.cryptography.enums.Mode;
import bee01.humbat.keydistributioncenter.cryptography.interfaces.Cryptable;
import bee01.humbat.keydistributioncenter.cryptography.keys.AsymmetricKey;
import bee01.humbat.keydistributioncenter.cryptography.keys.Key;
import bee01.humbat.keydistributioncenter.cryptography.pojos.ConfigPojo;
import bee01.humbat.keydistributioncenter.cryptography.pojos.KeyPojo;
import bee01.humbat.keydistributioncenter.cryptography.pojos.ModePojo;
import bee01.humbat.keydistributioncenter.dtos.DecryptedMessageDTO;
import bee01.humbat.keydistributioncenter.dtos.MessageDTO;
import bee01.humbat.keydistributioncenter.entities.Message;
import bee01.humbat.keydistributioncenter.entities.User;
import bee01.humbat.keydistributioncenter.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class MessageService {
    private static final Logger logger = Logger.getLogger(MessageService.class.getName());

    private final MessageRepository repo;
    private final UserService userService;

    public MessageService(MessageRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    public Message toMessage(MessageDTO dto) {
        logger.info("Starting to convert MessageDTO to Message entity...");

        Message message = new Message();

        String rawAlgo = dto.algorithm();
        String rawMode = dto.mode();
        String rawKey = dto.key();
        logger.info("Received algorithm: " + rawAlgo);
        logger.info("Received mode: " + rawMode);
        logger.info("Received symmetric key: " + rawKey);

        User from = dto.from();
        User to = userService.findById(dto.to());
        logger.info("Sender: " + from.getUsername());
        logger.info("Receiver: " + to.getUsername());

        Algorithm algo = Algorithm.getAlgorithm(rawAlgo);
        Mode mode = Mode.getMode(rawMode);
        Key key = Key.giveKey(rawKey, algo);
        String text = dto.text();

        logger.info("Algorithm object resolved: " + algo);
        logger.info("Mode object resolved: " + mode);
        logger.info("Plaintext to encrypt: " + text);

        KeyPojo keyPojo = new KeyPojo(algo, key);
        ModePojo modePojo = new ModePojo(mode);
        ConfigPojo config = new ConfigPojo(keyPojo, modePojo);

        logger.info("ConfigPojo created successfully");

        AsymmetricKey publicKey = new AsymmetricKey(to.getPublicKey());
        logger.info("Public key of receiver loaded");

        RsaCipher rsaCipher = new RsaCipher(publicKey);
        CryptEngine<RsaCipher> keyEngine = new CryptEngine<>(rsaCipher);
        String encryptedKey = keyEngine.encrypt(key.toString());
        logger.info("Symmetric key encrypted with RSA");
        logger.info("Encrypted symmetric key (RSA): " + encryptedKey);

        logger.info("Symmetric key encrypted with RSA");

        CryptEngine<?> textEngine = new CryptEngine<>(config);
        String encryptedText = textEngine.encrypt(text);
        logger.info("Text encrypted with symmetric algorithm");
        logger.info("Encrypted message text: " + encryptedText);
        logger.info("Text encrypted with symmetric algorithm");



        message.setSender(from);
        message.setReceiver(to);
        message.setEncryptedSymmetricKey(encryptedKey);
        message.setEncryptedText(encryptedText);
        message.setAlgorithm(algo.name());
        message.setMode(mode.name());
        message.setSentAt(LocalDateTime.now());

        logger.info("Message object fully built and ready to be saved.");

        return message;
    }

    public DecryptedMessageDTO toDecryptedMessageDTO(Message message) {
        logger.info("Starting to decrypt message with ID: " + message.getId());

        String encryptedSymmetricKey = message.getEncryptedSymmetricKey();
        String encryptedText = message.getEncryptedText();
        String algoName = message.getAlgorithm();
        String modeName = message.getMode();

        User receiver = message.getReceiver();
        AsymmetricKey privateKey = new AsymmetricKey(receiver.getPrivateKey());
        logger.info("Private key of receiver loaded");

        // 1. RSA ilə açarı deşifrə et
        RsaCipher rsaCipher = new RsaCipher(privateKey);
        CryptEngine<RsaCipher> keyEngine = new CryptEngine<>(rsaCipher);
        String rawSymmetricKey = keyEngine.decrypt(encryptedSymmetricKey);
        logger.info("Decrypted symmetric key: " + rawSymmetricKey);

        // 2. Algoritm və modeni qur
        Algorithm algo = Algorithm.getAlgorithm(algoName);
        Mode mode = Mode.getMode(modeName);
        Key key = Key.giveKey(rawSymmetricKey, algo);

        KeyPojo keyPojo = new KeyPojo(algo, key);
        ModePojo modePojo = new ModePojo(mode);
        ConfigPojo config = new ConfigPojo(keyPojo, modePojo);

        // 3. Mesajı decrypt et
        CryptEngine<?> textEngine = new CryptEngine<>(config);
        String plainText = textEngine.decrypt(encryptedText);
        logger.info("Decrypted text: " + plainText);

        // 4. DTO qaytar
        return new DecryptedMessageDTO(
                message,
                plainText,
                rawSymmetricKey
        );
    }



    public void sendMessage(MessageDTO dto) {
        Message message = toMessage(dto);
        repo.save(message);
    }
    public List<DecryptedMessageDTO> findByReceiver(User receiver) {
        return repo.findByReceiver(receiver).stream().map(this::toDecryptedMessageDTO).toList();
    }
    public List<DecryptedMessageDTO> findBySender(User sender) {
        return repo.findBySender(sender).stream().map(this::toDecryptedMessageDTO).toList();
    }
}
