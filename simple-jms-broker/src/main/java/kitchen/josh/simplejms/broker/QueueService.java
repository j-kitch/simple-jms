package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.OldMessage;

import java.util.*;

/**
 * A class implementing a point-to-point destination for the broker.
 */
public class QueueService implements SingleDestinationService {

    private final Set<UUID> consumers;
    private final Set<UUID> producers;
    private final Queue<OldMessage> messages;

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
        verifyConsumerExists(consumer);
        consumers.remove(consumer);
    }

    @Override
    public void removeProducer(UUID producer) {
        verifyProducerExists(producer);
        producers.remove(producer);
    }

    @Override
    public void addMessage(UUID producer, OldMessage message) {
        verifyProducerExists(producer);
        messages.add(message);
    }

    @Override
    public Optional<OldMessage> readMessage(UUID consumerId) {
        verifyConsumerExists(consumerId);
        return Optional.ofNullable(messages.poll());
    }

    Set<UUID> getConsumers() {
        return consumers;
    }

    Set<UUID> getProducers() {
        return producers;
    }

    Queue<OldMessage> getMessages() {
        return messages;
    }

    private void verifyConsumerExists(UUID consumerId) {
        if (!consumers.contains(consumerId)) {
            throw new ConsumerDoesNotExistException();
        }
    }

    private void verifyProducerExists(UUID producerId) {
        if (!producers.contains(producerId)) {
            throw new ProducerDoesNotExistException();
        }
    }
}
