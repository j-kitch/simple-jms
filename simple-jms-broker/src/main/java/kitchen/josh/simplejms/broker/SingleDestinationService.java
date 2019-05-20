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
     * Create a new consumer for this destination.
     * TODO: We're removing creation of consumers from the SingleDestinationService, this will now be handled externally
     *      * by other parts of the system.  These will instead call the {@link SingleDestinationService#addConsumer(UUID)}
     *      * to notify the service about the consumer.
     *
     * @return the id of the new consumer
     * @deprecated
     */
    UUID createConsumer();

    /**
     * Create a new producer for this destination.
     * TODO: We're removing creation of producers from the SingleDestinationService, this will now be handled externally
     *      * by other parts of the system.  These will instead call the {@link SingleDestinationService#addProducer(UUID)}
     *      * to notify the service about the producer.
     *
     * @return the id of the producer
     * @deprecated
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
    void addMessage(UUID producerId, Message message);

    /**
     * Receive and remove a message from the destination for a consumer.
     *
     * @param consumerId the id of the consumer receiving the message
     * @return the message received from the destination, or <code>Optional.empty()</code> if there isn't a message
     * @throws ConsumerDoesNotExistException if the consumer doesn't exist
     */
    Optional<Message> readMessage(UUID consumerId);
}
