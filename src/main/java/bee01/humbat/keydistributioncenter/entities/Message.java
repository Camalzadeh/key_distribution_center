package bee01.humbat.keydistributioncenter.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    private String algorithm;

    private String mode;

    @Column(columnDefinition = "TEXT")
    private String encryptedSymmetricKey;

    @Column(columnDefinition = "TEXT")
    private String encryptedText;

    private LocalDateTime sentAt;

    public Message(User sender, User receiver, String algorithm, String mode, String encryptedSymmetricKey, String encryptedText) {
        this.sender = sender;
        this.receiver = receiver;
        this.algorithm = algorithm;
        this.mode = mode;
        this.encryptedSymmetricKey = encryptedSymmetricKey;
        this.encryptedText = encryptedText;
        this.sentAt = LocalDateTime.now();
    }

    public Message() {

    }

    public Long getId() {
        return id;
    }
    public User getSender() {
        return sender;
    }
    public User getReceiver() {
        return receiver;
    }
    public String getAlgorithm() {
        return algorithm;
    }
    public String getEncryptedSymmetricKey() {
        return encryptedSymmetricKey;
    }
    public String getEncryptedText() {
        return encryptedText;
    }
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setSender(User sender) {
        this.sender = sender;
    }
    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    public void setEncryptedSymmetricKey(String encryptedSymmetricKey) {
        this.encryptedSymmetricKey = encryptedSymmetricKey;
    }
    public void setEncryptedText(String encryptedText) {
        this.encryptedText = encryptedText;
    }
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
}
