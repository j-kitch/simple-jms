package kitchen.josh.simplejms.broker;

import java.util.*;

public class SingleTopicService implements SingleDestinationService {

    private final Map<UUID, Queue<String>> consumerQueues;
    private final Set<UUID> producers;

    SingleTopicService() {
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
        consumerQueues.remove(consumerId);
    }

    @Override
    public void removeProducer(UUID producerId) {
        producers.remove(producerId);
    }

    @Override
    public void addMessage(UUID producer, String message) {
        if (producers.contains(producer)) {
            consumerQueues.values().forEach(queue -> queue.add(message));
        }
    }

    @Override
    public Optional<String> readMessage(UUID consumerId) {
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
