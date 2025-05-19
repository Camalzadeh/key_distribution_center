package bee01.humbat.keydistributioncenter.services;

import bee01.humbat.keydistributioncenter.cryptography.KeyDistributionCenter;
import bee01.humbat.keydistributioncenter.cryptography.ciphers.RsaCipher;
import bee01.humbat.keydistributioncenter.dtos.UserDTO;
import bee01.humbat.keydistributioncenter.entities.User;
import bee01.humbat.keydistributioncenter.repositories.UserRepository;
import bee01.humbat.keydistributioncenter.cryptography.keys.AsymmetricKey;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User saveUser(UserDTO dto) {
        AsymmetricKey[] keyPair = KeyDistributionCenter.generateKeyPair(1024);
        AsymmetricKey publicKey = keyPair[0];
        AsymmetricKey privateKey = keyPair[1];

        String encryptedPassword = RsaCipher.encrypt(dto.password(), publicKey);

        User user = new User();
        user.setUsername(dto.username());
        user.setEncryptedPassword(encryptedPassword);
        user.setPublicKey(publicKey.toString());
        user.setPrivateKey(privateKey.toString());
        user.setCreatedAt(LocalDateTime.now());

        return repository.save(user);
    }

    public Optional<User> login(UserDTO dto) {
        return repository.findByUsername(dto.username())
                .filter(user -> {
                    AsymmetricKey privateKey = new AsymmetricKey(user.getPrivateKey());

                    String decryptedPassword = RsaCipher.decrypt(user.getEncryptedPassword(), privateKey);

                    return decryptedPassword.equals(dto.password());
                });
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    public User findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public boolean existsByEncryptedPassword(String encryptedPassword) {
        return repository.existsByEncryptedPassword(encryptedPassword);
    }

}
