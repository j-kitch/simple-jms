package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;

import java.util.*;

/**
 * A class implementing a publish-subscribe model of destination.
 */
public class TopicService implements SingleDestinationService {

    private final UUID id;
    private final Map<UUID, Queue<Message>> consumerQueues;
    private final Set<UUID> producers;

    TopicService(UUID id) {
        this.id = id;
        consumerQueues = new HashMap<>();
        producers = new HashSet<>();
    }

    @Override
    public void addConsumer(UUID consumerId) {
        if (consumerQueues.containsKey(consumerId)) {
            throw new IllegalStateException("Consumer " + consumerId + " already consuming from topic");
        }
        consumerQueues.put(consumerId, new LinkedList<>());
    }

    @Override
    public void addProducer(UUID producerId) {
        if (producers.contains(producerId)) {
            throw new IllegalStateException("Producer " + producerId + " already producing to topic");
        }
        producers.add(producerId);
    }

    @Override
    public void removeConsumer(UUID consumerId) {
        verifyConsumerExists(consumerId);
        consumerQueues.remove(consumerId);
    }

    @Override
    public void removeProducer(UUID producerId) {
        verifyProducerExists(producerId);
        producers.remove(producerId);
    }

    @Override
    public void addMessage(UUID producer, Message message) {
        verifyProducerExists(producer);
        message.setDestination(new Destination(DestinationType.TOPIC, id));
        message.setId("ID:" + UUID.randomUUID());
        consumerQueues.values().forEach(queue -> queue.add(message));
    }

    @Override
    public Optional<Message> deliverMessage(UUID consumerId) {
        verifyConsumerExists(consumerId);
        return Optional.ofNullable(consumerQueues.get(consumerId).poll());
    }

    Map<UUID, Queue<Message>> getConsumerQueues() {
        return consumerQueues;
    }

    Set<UUID> getProducers() {
        return producers;
    }

    private void verifyProducerExists(UUID producerId) {
        if (!producers.contains(producerId)) {
            throw new ProducerDoesNotExistException();
        }
    }

    private void verifyConsumerExists(UUID consumerId) {
        if (!consumerQueues.containsKey(consumerId)) {
            throw new ConsumerDoesNotExistException();
        }
    }
}
