package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConsumerManager {

    private final DestinationService destinationService;
    private final Map<UUID, SingleConsumerService> consumers;

    public ConsumerManager(DestinationService destinationService) {
        this.destinationService = destinationService;
        this.consumers = new HashMap<>();
    }

    public UUID createConsumer(Destination destination) {
        SingleDestinationService singleDestinationService = destinationService.findDestination(destination)
                .orElseThrow(DestinationDoesNotExistException::new);
        UUID consumerId = UUID.randomUUID();
        singleDestinationService.addConsumer(consumerId);
        consumers.put(consumerId, new SingleConsumerService(consumerId, singleDestinationService));
        return consumerId;
    }

    public Optional<SingleConsumerService> findConsumer(UUID consumerId) {
        return Optional.ofNullable(consumers.get(consumerId));
    }

    public void removeConsumer(UUID consumerId) {
        SingleConsumerService consumerService = Optional.ofNullable(consumers.remove(consumerId))
                .orElseThrow(ConsumerDoesNotExistException::new);
        consumerService.close();
    }
}
