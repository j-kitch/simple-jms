package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.message.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProducerService {

    private final DestinationService destinationService;
    private final Map<UUID, Destination> producers;

    public ProducerService(DestinationService destinationService) {
        this.destinationService = destinationService;
        this.producers = new HashMap<>();
    }

    public UUID createProducer(Destination destination) {
        SingleDestinationService singleDestinationService = destinationService.findDestination(destination)
                .orElseThrow(DestinationDoesNotExistException::new);
        UUID producerId = UUID.randomUUID();
        singleDestinationService.addProducer(producerId);
        producers.put(producerId, destination);
        return producerId;
    }

    public void sendMessage(UUID producerId, Message message) {
        findDestination(producerId).addMessage(producerId, message);
    }

    public void removeProducer(UUID producerId) {
        findDestination(producerId).removeProducer(producerId);
        producers.remove(producerId);
    }

    private SingleDestinationService findDestination(UUID producerId) {
        return Optional.ofNullable(producers.get(producerId))
                .flatMap(destinationService::findDestination)
                .orElseThrow(ProducerDoesNotExistException::new);
    }
}
