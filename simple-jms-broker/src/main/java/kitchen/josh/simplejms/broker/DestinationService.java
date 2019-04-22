package kitchen.josh.simplejms.broker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DestinationService {

    private final Map<UUID, SingleDestinationService> queues;
    private final Map<UUID, SingleDestinationService> topics;

    DestinationService() {
        queues = new HashMap<>();
        topics = new HashMap<>();
    }

    UUID createDestination(Destination destination) {
        UUID id = UUID.randomUUID();
        if (destination == Destination.QUEUE) {
            queues.put(id, new SingleQueueService());
        } else if (destination == Destination.TOPIC) {
            topics.put(id, new SingleTopicService());
        }
        return id;
    }

    Optional<SingleDestinationService> findDestination(Destination type, UUID id) {
        if (type == Destination.QUEUE) {
            return Optional.ofNullable(queues.get(id));
        } else if (type == Destination.TOPIC) {
            return Optional.ofNullable(topics.get(id));
        } else {
            return Optional.empty();
        }
    }

    Map<UUID, SingleDestinationService> getQueues() {
        return queues;
    }

    Map<UUID, SingleDestinationService> getTopics() {
        return topics;
    }
}
