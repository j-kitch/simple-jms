package kitchen.josh.simplejms.broker;

import java.util.*;

public class QueueService implements SingleDestinationService {

    private final Set<UUID> consumers;
    private final Set<UUID> producers;
    private final Queue<String> messages;

    QueueService() {
        consumers = new HashSet<>();
        producers = new HashSet<>();
        messages = new LinkedList<>();
    }

    @Override
    public UUID createConsumer() {
        UUID consumerId = UUID.randomUUID();
        consumers.add(consumerId);
        return consumerId;
    }

    @Override
    public UUID createProducer() {
        UUID producerId = UUID.randomUUID();
        producers.add(producerId);
        return producerId;
    }

    @Override
    public void removeConsumer(UUID consumer) {
        consumers.remove(consumer);
    }

    @Override
    public void removeProducer(UUID producer) {
        producers.remove(producer);
    }

    @Override
    public void addMessage(UUID producer, String message) {
        if (producers.contains(producer)) {
            messages.add(message);
        }
    }

    @Override
    public Optional<String> readMessage(UUID consumerId) {
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
