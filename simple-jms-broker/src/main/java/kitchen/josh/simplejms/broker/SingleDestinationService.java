package kitchen.josh.simplejms.broker;

import java.util.Optional;
import java.util.UUID;

public interface SingleDestinationService {

    UUID createConsumer();

    UUID createProducer();

    void removeConsumer(UUID consumer);

    void removeProducer(UUID producer);

    void addMessage(UUID producer, String message);

    Optional<String> readMessage(UUID consumer);
}
