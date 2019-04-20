package kitchen.josh.simplejms.broker;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
class QueueService {

    private final Set<UUID> consumers;
    private final Queue<String> messages;

    QueueService() {
        consumers = new HashSet<>();
        messages = new LinkedList<>();
    }

    UUID createConsumer() {
        UUID consumerId = UUID.randomUUID();
        consumers.add(consumerId);
        return consumerId;
    }

    void addMessage(String message) {
        messages.add(message);
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

    Queue<String> getMessages() {
        return messages;
    }
}
