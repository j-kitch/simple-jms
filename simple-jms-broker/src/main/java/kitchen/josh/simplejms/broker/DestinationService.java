package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * A service for creating, looking up and maintaining the lifetimes of destinations in the broker.
 */
@Component
public class DestinationService {

    private static final Map<DestinationType, Function<UUID, SingleDestinationService>> SERVICE_SUPPLIERS = createServiceSupplierMap();

    private final Map<Destination, SingleDestinationService> destinations;

    DestinationService() {
        destinations = new HashMap<>();
    }

    /**
     * Create a new destination of the given type.
     *
     * @param destinationType the type of destination to create
     * @return the id of the destination created
     */
    public UUID createDestination(DestinationType destinationType) {
        Destination destination = new Destination(destinationType, UUID.randomUUID());
        destinations.put(destination, createService(destination));
        return destination.getId();
    }

    /**
     * Find a destination service in the broker.
     *
     * @param destination the destination
     * @return the destination searched for, or <code>Optional.empty()</code> if it doesn't exist
     */
    public Optional<SingleDestinationService> findDestination(Destination destination) {
        return Optional.ofNullable(destinations.get(destination));
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

    private static Map<DestinationType, Function<UUID, SingleDestinationService>> createServiceSupplierMap() {
        Map<DestinationType, Function<UUID, SingleDestinationService>> suppliers = new HashMap<>();
        suppliers.put(DestinationType.TOPIC, TopicService::new);
        suppliers.put(DestinationType.QUEUE, QueueService::new);
        return suppliers;
    }

    private static SingleDestinationService createService(Destination destination) {
        return SERVICE_SUPPLIERS.get(destination.getType()).apply(destination.getId());
    }
}
