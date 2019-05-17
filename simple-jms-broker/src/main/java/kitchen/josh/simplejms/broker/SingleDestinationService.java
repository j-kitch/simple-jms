package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.TextMessage;

import java.util.Optional;
import java.util.UUID;

/**
 * The API for interacting with a destination.
 */
public interface SingleDestinationService {

    /**
     * Create a new consumer for this destination.
     *
     * @return the id of the new consumer
     */
    UUID createConsumer();

    /**
     * Create a new producer for this destination.
     *
     * @return the id of the producer
     */
    UUID createProducer();

    /**
     * Remove a consumer from this destination.
     *
     * @param consumerId the id of the consumer
     * @throws ConsumerDoesNotExistException if the consumer doesn't exist
     */
    void removeConsumer(UUID consumerId);

    /**
     * Remove a producer from this destination.
     *
     * @param producerId the id of the producer
     * @throws ProducerDoesNotExistException if the producer doesn't exist
     */
    void removeProducer(UUID producerId);

    /**
     * Send a message to the destination from a producer.
     *
     * @param producerId the id of the producer sending the message
     * @param message    the message sent to the destination
     * @throws ProducerDoesNotExistException if the producer doesn't exist
     */
    void addMessage(UUID producerId, TextMessage message);

    /**
     * Receive and remove a message from the destination for a consumer.
     *
     * @param consumerId the id of the consumer receiving the message
     * @return the message received from the destination, or <code>Optional.empty()</code> if there isn't a message
     * @throws ConsumerDoesNotExistException if the consumer doesn't exist
     */
    Optional<TextMessage> readMessage(UUID consumerId);
}
