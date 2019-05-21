package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.message.Message;

import java.io.Closeable;
import java.util.Optional;
import java.util.UUID;

public class SingleConsumerService implements Closeable {

    private final UUID consumerId;
    private final SingleDestinationService destinationService;

    public SingleConsumerService(UUID consumerId, SingleDestinationService destinationService) {
        this.consumerId = consumerId;
        this.destinationService = destinationService;
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
        return null;
    }

    /**
     * Acknowledge the receipt of all messages before and including this message.
     *
     * @param messageId the id of message to acknowledge
     */
    public void acknowledge(String messageId) {

    }

    /**
     * Reset the consumer to receive all unacknowledged messages it has previously received.
     */
    public void recover() {

    }

    /**
     * Remove the consumer.
     */
    @Override
    public void close() {

    }
}
