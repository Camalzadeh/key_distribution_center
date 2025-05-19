package bee01.humbat.keydistributioncenter.services;

import bee01.humbat.keydistributioncenter.dtos.UserDTO;
import bee01.humbat.keydistributioncenter.entities.Session;
import bee01.humbat.keydistributioncenter.entities.User;
import bee01.humbat.keydistributioncenter.repositories.SessionRepository;
import bee01.humbat.keydistributioncenter.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository repository;
    private final UserRepository userRepository;

    @Value("${session.ttl.hours}")
    private int sessionTtlHours;

    public SessionService(SessionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Session createSession(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Session session = new Session(
                null,
                token,
                user,
                now,
                now.plusHours(sessionTtlHours)
        );
        return repository.save(session);
    }

    public Optional<Session> findByToken(String sessionId) {
        return repository.findBySessionId(sessionId)
                .filter(s -> s.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Transactional
    public void deleteByToken(String sessionId) {
        repository.deleteBySessionId(sessionId);
    }
}
