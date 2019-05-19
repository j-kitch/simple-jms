package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Message;

import java.util.*;

/**
 * A class implementing a publish-subscribe model of destination.
 */
public class TopicService implements SingleDestinationService {

    private final Map<UUID, Queue<Message>> consumerQueues;
    private final Set<UUID> producers;

    TopicService() {
        consumerQueues = new HashMap<>();
        producers = new HashSet<>();
    }

    @Override
    public UUID createConsumer() {
        UUID consumerId = UUID.randomUUID();
        consumerQueues.put(consumerId, new LinkedList<>());
        return consumerId;
    }

    @Override
    public UUID createProducer() {
        UUID producerId = UUID.randomUUID();
        producers.add(producerId);
        return producerId;
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
        consumerQueues.values().forEach(queue -> queue.add(message));
    }

    @Override
    public Optional<Message> readMessage(UUID consumerId) {
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
