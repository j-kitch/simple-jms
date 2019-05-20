package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<SingleDestinationService> findConsumerDestination(UUID consumerId) {
        return Optional.ofNullable(consumers.get(consumerId))
                .flatMap(destinationService::findDestination);
    }

    public void removeConsumer(UUID consumerId) {
        findConsumerDestination(consumerId)
                .orElseThrow(ConsumerDoesNotExistException::new)
                .removeConsumer(consumerId);
        consumers.remove(consumerId);
    }
}
