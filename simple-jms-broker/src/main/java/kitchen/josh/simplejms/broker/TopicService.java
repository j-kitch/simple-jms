package kitchen.josh.simplejms.broker;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
class TopicService {

    private final Map<UUID, Queue<String>> consumerQueues;

    TopicService() {
        consumerQueues = new HashMap<>();
    }

    UUID createConsumer() {
        UUID consumerId = UUID.randomUUID();
        consumerQueues.put(consumerId, new LinkedList<>());
        return consumerId;
    }

    void addMessage(String message) {
        consumerQueues.values().forEach(queue -> queue.add(message));
    }

    Optional<String> readMessage(UUID consumerId) {
        return Optional.ofNullable(consumerQueues.get(consumerId))
                .map(Queue::poll);
    }

    Map<UUID, Queue<String>> getConsumerQueues() {
        return consumerQueues;
    }
}
