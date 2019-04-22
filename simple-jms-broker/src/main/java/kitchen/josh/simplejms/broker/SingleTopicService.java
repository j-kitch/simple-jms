package kitchen.josh.simplejms.broker;

import java.util.*;

public class SingleTopicService {

    private final Map<UUID, Queue<String>> consumerQueues;
    private final Set<UUID> producers;

    SingleTopicService() {
        consumerQueues = new HashMap<>();
        producers = new HashSet<>();
    }

    UUID createConsumer() {
        UUID consumerId = UUID.randomUUID();
        consumerQueues.put(consumerId, new LinkedList<>());
        return consumerId;
    }

    UUID createProducer() {
        UUID producerId = UUID.randomUUID();
        producers.add(producerId);
        return producerId;
    }

    void addMessage(UUID producer, String message) {
        if (producers.contains(producer)) {
            consumerQueues.values().forEach(queue -> queue.add(message));
        }
    }

    Optional<String> readMessage(UUID consumerId) {
        return Optional.ofNullable(consumerQueues.get(consumerId))
                .map(Queue::poll);
    }

    Map<UUID, Queue<String>> getConsumerQueues() {
        return consumerQueues;
    }

    Set<UUID> getProducers() {
        return producers;
    }
}
