package kitchen.josh.simplejms.broker;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class DestinationService {

    private final Map<UUID, SingleDestinationService> queues;
    private final Map<UUID, SingleDestinationService> topics;

    DestinationService() {
        queues = new HashMap<>();
        topics = new HashMap<>();
    }

    UUID createDestination(DestinationType destinationType) {
        UUID id = UUID.randomUUID();
        if (destinationType == DestinationType.QUEUE) {
            queues.put(id, new SingleQueueService());
        } else if (destinationType == DestinationType.TOPIC) {
            topics.put(id, new SingleTopicService());
        }
        return id;
    }

    Optional<SingleDestinationService> findDestination(DestinationType type, UUID id) {
        if (type == DestinationType.QUEUE) {
            return Optional.ofNullable(queues.get(id));
        } else if (type == DestinationType.TOPIC) {
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
