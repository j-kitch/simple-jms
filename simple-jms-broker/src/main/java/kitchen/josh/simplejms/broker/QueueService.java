package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;

import java.util.*;

/**
 * A class implementing a point-to-point destination for the broker.
 */
public class QueueService implements SingleDestinationService {

    private final UUID id;
    private final Set<UUID> consumers;
    private final Set<UUID> producers;
    private final Queue<Message> messages;

    QueueService(UUID id) {
        this.id = id;
        consumers = new HashSet<>();
        producers = new HashSet<>();
        messages = new LinkedList<>();
    }

    @Override
    public void addConsumer(UUID consumerId) {
        if (consumers.contains(consumerId)) {
            throw new IllegalStateException("Consumer " + consumerId + " already consuming from queue");
        }
        consumers.add(consumerId);
    }

    @Override
    public void addProducer(UUID producerId) {
        if (producers.contains(producerId)) {
            throw new IllegalStateException("Producer " + producerId + " already producing to queue");
        }
        producers.add(producerId);
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
    public void addMessage(UUID producer, Message message) {
        verifyProducerExists(producer);
        message.setDestination(new Destination(DestinationType.QUEUE, id));
        message.setId("ID:" + UUID.randomUUID());
        messages.add(message);
    }

    @Override
    public Optional<Message> deliverMessage(UUID consumerId) {
        verifyConsumerExists(consumerId);
        return Optional.ofNullable(messages.poll());
    }

    Set<UUID> getConsumers() {
        return consumers;
    }

    Set<UUID> getProducers() {
        return producers;
    }

    Queue<Message> getMessages() {
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
