package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.TextMessage;

import java.util.*;

/**
 * A class implementing a publish-subscribe model of destination.
 */
public class TopicService implements SingleDestinationService {

    private final Map<UUID, Queue<TextMessage>> consumerQueues;
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
    public void addMessage(UUID producer, TextMessage message) {
        verifyProducerExists(producer);
        consumerQueues.values().forEach(queue -> queue.add(message));
    }

    @Override
    public Optional<TextMessage> readMessage(UUID consumerId) {
        verifyConsumerExists(consumerId);
        return Optional.ofNullable(consumerQueues.get(consumerId).poll());
    }

    Map<UUID, Queue<TextMessage>> getConsumerQueues() {
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
