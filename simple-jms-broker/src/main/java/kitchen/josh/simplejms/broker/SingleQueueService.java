package kitchen.josh.simplejms.broker;

import java.util.*;

public class SingleQueueService {

    private final Set<UUID> consumers;
    private final Set<UUID> producers;
    private final Queue<String> messages;

    SingleQueueService() {
        consumers = new HashSet<>();
        producers = new HashSet<>();
        messages = new LinkedList<>();
    }

    UUID createConsumer() {
        UUID consumerId = UUID.randomUUID();
        consumers.add(consumerId);
        return consumerId;
    }

    UUID createProducer() {
        UUID producerId = UUID.randomUUID();
        producers.add(producerId);
        return producerId;
    }

    void removeConsumer(UUID consumer) {
        consumers.remove(consumer);
    }

    void removeProducer(UUID producer) {
        producers.remove(producer);
    }

    void addMessage(UUID producer, String message) {
        if (producers.contains(producer)) {
            messages.add(message);
        }
    }

    Optional<String> readMessage(UUID consumerId) {
        if (!consumers.contains(consumerId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(messages.poll());
    }

    Set<UUID> getConsumers() {
        return consumers;
    }

    Set<UUID> getProducers() {
        return producers;
    }

    Queue<String> getMessages() {
        return messages;
    }
}
