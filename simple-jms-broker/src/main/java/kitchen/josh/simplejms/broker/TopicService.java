package kitchen.josh.simplejms.broker;

import java.util.*;

class TopicService {

    private final Map<UUID, Queue<String>> consumerMap;

    TopicService() {
        consumerMap = new HashMap<>();
    }

    UUID createConsumer() {
        UUID consumerId = UUID.randomUUID();
        consumerMap.put(consumerId, new LinkedList<>());
        return consumerId;
    }

    void addMessage(String message) {
        consumerMap.values().forEach(queue -> queue.add(message));
    }

    Map<UUID, Queue<String>> getConsumerMap() {
        return consumerMap;
    }
}
