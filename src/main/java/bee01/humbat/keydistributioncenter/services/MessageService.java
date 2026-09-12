package bee01.humbat.keydistributioncenter.services;

import bee01.humbat.keydistributioncenter.cryptography.CipherText;
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

@Service
public class MessageService {

    private final MessageRepository repo;
    private final UserService userService;

    public MessageService(MessageRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    public Message toMessage(MessageDTO dto) {
        Message message = new Message();

        User from = dto.from();
        User to = userService.findById(dto.to());

        Algorithm algo = Algorithm.getAlgorithm(dto.algorithm());
        Mode mode = Mode.getMode(dto.mode());
        Key key = Key.giveKey(dto.key(), algo);

        ConfigPojo config = new ConfigPojo(new KeyPojo(algo, key), new ModePojo(mode));

        // The symmetric key is wrapped with the recipient's public key, so only
        // they can unwrap it. RsaCipher already returns Base64, which is safe
        // to store as text.
        RsaCipher rsaCipher = new RsaCipher(new AsymmetricKey(to.getPublicKey()));
        String encryptedKey = new CryptEngine<>(rsaCipher).encrypt(key.toString());

        CryptEngine<?> textEngine = new CryptEngine<>(config);
        String encryptedText = textEngine.encrypt(dto.text());

        message.setSender(from);
        message.setReceiver(to);
        message.setEncryptedSymmetricKey(encryptedKey);
        // Encoded, not stored raw: the classical ciphers can emit U+0000, and
        // PostgreSQL cannot hold a NUL in a text column. See CipherText.
        message.setEncryptedText(CipherText.store(encryptedText));
        message.setAlgorithm(algo.name());
        message.setMode(mode.name());
        message.setSentAt(LocalDateTime.now());

        return message;
    }

    public DecryptedMessageDTO toDecryptedMessageDTO(Message message) {
        // Nothing here is logged, deliberately. This method handles the
        // recipient's private key, the unwrapped symmetric key and the
        // plaintext; every one of those used to be written to the container
        // log at INFO level, which put the contents of every message on disk
        // and into the configuration backups.
        AsymmetricKey privateKey = new AsymmetricKey(message.getReceiver().getPrivateKey());

        RsaCipher rsaCipher = new RsaCipher(privateKey);
        String rawSymmetricKey = new CryptEngine<>(rsaCipher)
                .decrypt(message.getEncryptedSymmetricKey());

        Algorithm algo = Algorithm.getAlgorithm(message.getAlgorithm());
        Mode mode = Mode.getMode(message.getMode());
        Key key = Key.giveKey(rawSymmetricKey, algo);

        ConfigPojo config = new ConfigPojo(new KeyPojo(algo, key), new ModePojo(mode));

        CryptEngine<?> textEngine = new CryptEngine<>(config);
        String plainText = textEngine.decrypt(CipherText.load(message.getEncryptedText()));

        return new DecryptedMessageDTO(message, plainText, rawSymmetricKey);
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
