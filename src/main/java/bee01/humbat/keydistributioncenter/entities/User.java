package bee01.humbat.keydistributioncenter.entities;

import bee01.humbat.keydistributioncenter.dtos.UserDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(length = 4096)
    private String encryptedPassword;
    @Column(length = 4096)
    private String publicKey;
    @Column(length = 4096)
    private String privateKey;

    private LocalDateTime createdAt;

    // No-arg constructor (required by JPA)
    public User() {
    }

    // All-args constructor
    public User(Long id, String username, String encryptedPassword,
                String rsaEncryptedSymmetricKey, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.publicKey = rsaEncryptedSymmetricKey;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String rsaEncryptedSymmetricKey) {
        this.publicKey = rsaEncryptedSymmetricKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO toDto() {
        return new UserDTO( username, encryptedPassword );
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", rsaEncryptedSymmetricKey='" + publicKey + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
