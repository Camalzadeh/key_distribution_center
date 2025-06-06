package bee01.humbat.keydistributioncenter.repositories;

import bee01.humbat.keydistributioncenter.entities.Message;
import bee01.humbat.keydistributioncenter.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySender(User sender);
    List<Message> findByReceiver(User receiver);
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    List<Message> findByReceiverAndSender(User receiver, User sender);
    List<Message> findBySenderOrReceiver(User sender, User receiver);
    List<Message> findBySenderAndReceiverOrReceiverAndSender(User sender, User receiver, User sender2, User receiver2);
}
