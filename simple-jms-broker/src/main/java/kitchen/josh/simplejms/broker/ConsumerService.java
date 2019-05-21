package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.message.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConsumerService {

    private final DestinationService destinationService;
    private final Map<UUID, Destination> consumers;

    public ConsumerService(DestinationService destinationService) {
        this.destinationService = destinationService;
        this.consumers = new HashMap<>();
    }

    public UUID createConsumer(Destination destination) {
        SingleDestinationService singleDestinationService = destinationService.findDestination(destination)
                .orElseThrow(DestinationDoesNotExistException::new);
        UUID consumerId = UUID.randomUUID();
        singleDestinationService.addConsumer(consumerId);
        consumers.put(consumerId, destination);
        return consumerId;
    }

    public Optional<Message> readMessage(UUID consumerId) {
        return findDestinationService(consumerId).deliverMessage(consumerId);
    }

    public void removeConsumer(UUID consumerId) {
        findDestinationService(consumerId).removeConsumer(consumerId);
        consumers.remove(consumerId);
    }

    private SingleDestinationService findDestinationService(UUID consumerId) {
        return Optional.ofNullable(consumers.get(consumerId))
                .flatMap(destinationService::findDestination)
                .orElseThrow(ConsumerDoesNotExistException::new);
    }
}
