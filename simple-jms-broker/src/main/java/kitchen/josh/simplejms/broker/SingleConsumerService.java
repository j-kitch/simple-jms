package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.message.Message;

import java.io.Closeable;
import java.util.*;

public class SingleConsumerService implements Closeable {

    private final UUID consumerId;
    private final SingleDestinationService destinationService;

    private Queue<Message> unacknowledged;
    private boolean inRecovery;

    public SingleConsumerService(UUID consumerId, SingleDestinationService destinationService) {
        this.consumerId = consumerId;
        this.destinationService = destinationService;
        this.unacknowledged = new LinkedList<>();
        this.inRecovery = false;
    }

    /**
     * Get the next message the consumer should receive.
     * <p>
     * If {@link SingleConsumerService#recover()} has been called, the message may be an old message that the consumer
     * failed to acknowledge.
     *
     * @return the next message, or <code>Optional.empty()</code> if the consumer has no further messages currently.
     */
    public Optional<Message> receive() {
        checkInRecovery();
        if (inRecovery) {
            return nextUnacknowledged();
        }
        return nextDelivered();
    }

    /**
     * Acknowledge the receipt of all messages before and including this message.
     *
     * @param messageId the id of message to acknowledge
     */
    public void acknowledge(String messageId) {
        Optional<Integer> messageIndex = indexOfMessageById(unacknowledged, messageId);
        messageIndex.ifPresent(index -> {
            unacknowledged = filterAfter(unacknowledged, index);
        });
    }

    /**
     * Reset the consumer to receive all unacknowledged messages it has previously received.
     */
    public void recover() {
        inRecovery = true;
    }

    /**
     * Remove the consumer.
     */
    @Override
    public void close() {
        destinationService.removeConsumer(consumerId);
    }

    private void checkInRecovery() {
        // Cannot recover messages that don't exist.
        inRecovery = inRecovery && unacknowledged.size() > 0;
    }

    private Optional<Message> nextUnacknowledged() {
        return Optional.of(unacknowledged.poll());
    }

    private Optional<Message> nextDelivered() {
        Optional<Message> delivered = destinationService.deliverMessage(consumerId);
        delivered.ifPresent(unacknowledged::add);
        return delivered;
    }

    private static Optional<Integer> indexOfMessageById(Queue<Message> queue, String messageId) {
        List<Message> messages = new ArrayList<>(queue);
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(messageId)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private static Queue<Message> filterAfter(Queue<Message> messages, int n) {
        return new LinkedList<>(new ArrayList<>(messages).subList(n + 1, messages.size()));
    }
}
