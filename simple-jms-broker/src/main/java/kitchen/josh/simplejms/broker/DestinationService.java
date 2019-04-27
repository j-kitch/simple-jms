package kitchen.josh.simplejms.broker;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

@Component
public class DestinationService {

    private static final Map<DestinationType, Supplier<SingleDestinationService>> SERVICE_SUPPLIERS = createServiceSupplierMap();

    private final Map<Destination, SingleDestinationService> destinations;

    DestinationService() {
        destinations = new HashMap<>();
    }

    UUID createDestination(DestinationType destinationType) {
        Destination destination = new Destination(destinationType, UUID.randomUUID());
        destinations.put(destination, createService(destinationType));
        return destination.getId();
    }

    Optional<SingleDestinationService> findDestination(DestinationType type, UUID id) {
        return Optional.ofNullable(destinations.get(new Destination(type, id)));
    }

    Map<UUID, SingleDestinationService> getQueues() {
        return destinations.keySet().stream()
                .filter(destination -> destination.getType() == DestinationType.QUEUE)
                .collect(toMap(Destination::getId, destinations::get));
    }

    Map<UUID, SingleDestinationService> getTopics() {
        return destinations.keySet().stream()
                .filter(destination -> destination.getType() == DestinationType.TOPIC)
                .collect(toMap(Destination::getId, destinations::get));
    }

    private static Map<DestinationType, Supplier<SingleDestinationService>> createServiceSupplierMap() {
        Map<DestinationType, Supplier<SingleDestinationService>> suppliers = new HashMap<>();
        suppliers.put(DestinationType.TOPIC, TopicService::new);
        suppliers.put(DestinationType.QUEUE, QueueService::new);
        return suppliers;
    }

    private static SingleDestinationService createService(DestinationType type) {
        return SERVICE_SUPPLIERS.get(type).get();
    }
}
