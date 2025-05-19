package bee01.humbat.keydistributioncenter.repositories;

import bee01.humbat.keydistributioncenter.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionId(String sessionId);


    Optional<Session> findByUserUsername(String user_username);

    boolean existsBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);
}
