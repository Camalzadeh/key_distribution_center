package bee01.humbat.keydistributioncenter.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String sessionId;

    @ManyToOne
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    // No-arg constructor (required by JPA)
    public Session() {}

    // All-args constructor
    public Session(Long id, String sessionId, User user,
                   LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
