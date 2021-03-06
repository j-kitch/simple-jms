package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.message.Message;

import java.util.Optional;
import java.util.UUID;

/**
 * The API for interacting with a destination.
 */
public interface SingleDestinationService {

    /**
     * Allocate resources for the consumer to start consuming from this destination.
     *
     * @param consumerId the id of the consumer to add
     * @throws IllegalStateException if the consumer is already connected to the destination
     */
    void addConsumer(UUID consumerId);

    /**
     * Allocate resources for the producer to start sending messages to this destination.
     *
     * @param producerId the id of the producer to add
     * @throws IllegalStateException if the producer is already connected to the destination
     */
    void addProducer(UUID producerId);

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
    void addMessage(UUID producerId, Message message);

    /**
     * Deliver the next message for the consumer.
     * <p>
     * Once a message has been delivered, it is the responsibility of the caller to handle message redelivery and
     * message acknowledgement.
     *
     * @param consumerId the id of the consumer to deliver the message to.
     * @return the message delivered from the destination, or <code>Optional.empty()</code> if there isn't a message
     * @throws ConsumerDoesNotExistException if the consumer doesn't exist
     */
    Optional<Message> deliverMessage(UUID consumerId);
}
