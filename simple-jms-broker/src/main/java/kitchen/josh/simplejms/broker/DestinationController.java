package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.jms.MessageFormatException;
import java.util.UUID;

import static java.util.Collections.emptyList;

/**
 * The controller for the broker's destinations.  With an API for creating destinations, producers and consumers,
 * and sending and receiving messages.
 */
@RestController
public class DestinationController {

    private static final String FAILED_CREATE_PRODUCER = "Failed to create producer";
    private static final String FAILED_CREATE_CONSUMER = "Failed to create consumer";
    private static final String FAILED_DELETE_PRODUCER = "Failed to delete producer";
    private static final String FAILED_DELETE_CONSUMER = "Failed to delete consumer";
    private static final String FAILED_SEND_MESSAGE = "Failed to send message";
    private static final String FAILED_RECEIVE_MESSAGE = "Failed to receive message";

    private static final String DESTINATION_DOES_NOT_EXIST = "the destination does not exist";
    private static final String PRODUCER_DOES_NOT_EXIST = "the producer does not exist";
    private static final String CONSUMER_DOES_NOT_EXIST = "the consumer does not exist";

    private final DestinationService destinationService;
    private final MessageModelFactory messageModelFactory;
    private final MessageFactory messageFactory;

    public DestinationController(DestinationService destinationService, MessageModelFactory messageModelFactory, MessageFactory messageFactory) {
        this.destinationService = destinationService;
        this.messageModelFactory = messageModelFactory;
        this.messageFactory = messageFactory;
    }

    /**
     * Create a new destination.
     *
     * @param destinationType the type of destination to create
     * @return the id of the created destination
     */
    @PostMapping(path = "/{destinationType}")
    public IdModel createDestination(@PathVariable String destinationType) {
        return new IdModel(destinationService.createDestination(toType(destinationType)));
    }

    /**
     * Create a new consumer for a destination.
     *
     * @param destinationType the type of destination to create a consumer for
     * @param destinationId   the id of the destination to create a consumer for
     * @return the id of the created consumer
     */
    @PostMapping(path = "/{destinationType}/{destinationId}/consumer")
    public IdModel createConsumer(@PathVariable String destinationType, @PathVariable UUID destinationId) {
        UUID consumerId = destinationService.findDestination(new Destination(toType(destinationType), destinationId))
                .map(SingleDestinationService::createConsumer)
                .orElseThrow(() -> createError(FAILED_CREATE_CONSUMER, DESTINATION_DOES_NOT_EXIST));
        return new IdModel(consumerId);
    }

    /**
     * Create a new producer for a destination.
     *
     * @param destinationType the type of destination to create a producer for
     * @param destinationId   the id of the destination to create a producer for
     * @return the id of the created consumer
     */
    @PostMapping(path = "/{destinationType}/{destinationId}/producer")
    public IdModel createProducer(@PathVariable String destinationType, @PathVariable UUID destinationId) {
        UUID consumerId = destinationService.findDestination(new Destination(toType(destinationType), destinationId))
                .map(SingleDestinationService::createProducer)
                .orElseThrow(() -> createError(FAILED_CREATE_PRODUCER, DESTINATION_DOES_NOT_EXIST));
        return new IdModel(consumerId);
    }

    /**
     * Delete a consumer.
     *
     * @param destinationType the type of destination
     * @param destinationId   the id of the destination
     * @param consumerId      the id of the consumer
     */
    @DeleteMapping(path = "/{destinationType}/{destinationId}/consumer/{consumerId}")
    public void deleteConsumer(@PathVariable String destinationType, @PathVariable UUID destinationId,
                               @PathVariable UUID consumerId) {
        try {
            destinationService.findDestination(new Destination(toType(destinationType), destinationId))
                    .orElseThrow(() -> createError(FAILED_DELETE_CONSUMER, DESTINATION_DOES_NOT_EXIST))
                    .removeConsumer(consumerId);
        } catch (ConsumerDoesNotExistException e) {
            throw createError(FAILED_DELETE_CONSUMER, CONSUMER_DOES_NOT_EXIST);
        }
    }

    /**
     * Delete a producer.
     *
     * @param destinationType the type of destination
     * @param destinationId   the id of the destination
     * @param producerId      the id of the producer
     */
    @DeleteMapping(path = "/{destinationType}/{destinationId}/producer/{producerId}")
    public void deleteProducer(@PathVariable String destinationType, @PathVariable UUID destinationId,
                               @PathVariable UUID producerId) {
        try {
            destinationService.findDestination(new Destination(toType(destinationType), destinationId))
                    .orElseThrow(() -> createError(FAILED_DELETE_PRODUCER, DESTINATION_DOES_NOT_EXIST))
                    .removeProducer(producerId);
        } catch (ProducerDoesNotExistException e) {
            throw createError(FAILED_DELETE_PRODUCER, PRODUCER_DOES_NOT_EXIST);
        }
    }

    /**
     * Send a message from a producer to a destination.
     *
     * @param destinationType the type of destination
     * @param destinationId   the id of the destination
     * @param producerId      the id of the producer
     * @param message         the message to send to the destination
     */
    @PostMapping(path = "/{destinationType}/{destinationId}/producer/{producerId}/send")
    public void sendMessage(@PathVariable String destinationType, @PathVariable UUID destinationId,
                            @PathVariable UUID producerId, @RequestBody MessageModel message) throws MessageFormatException {
        try {
            Destination destination = new Destination(toType(destinationType), destinationId);
            destinationService.findDestination(destination)
                    .orElseThrow(() -> createError(FAILED_SEND_MESSAGE, DESTINATION_DOES_NOT_EXIST))
                    .addMessage(producerId, messageFactory.create(message));
        } catch (ProducerDoesNotExistException e) {
            throw createError(FAILED_SEND_MESSAGE, PRODUCER_DOES_NOT_EXIST);
        }
    }

    /**
     * Receive a message for a consumer from a destination.
     *
     * @param destinationType the type of destination
     * @param destinationId   the id of the destination
     * @param consumerId      the id of the consumer
     * @return the message received from the destination
     */
    @PostMapping(path = "/{destinationType}/{destinationId}/consumer/{consumerId}/receive")
    public MessageModel receiveMessage(@PathVariable String destinationType, @PathVariable UUID destinationId,
                                       @PathVariable UUID consumerId) {
        try {
            return destinationService.findDestination(new Destination(toType(destinationType), destinationId))
                    .orElseThrow(() -> createError(FAILED_RECEIVE_MESSAGE, DESTINATION_DOES_NOT_EXIST))
                    .readMessage(consumerId)
                    .map(messageModelFactory::create)
                    .orElse(new MessageModel(null, emptyList(), null));
        } catch (ConsumerDoesNotExistException e) {
            throw createError(FAILED_RECEIVE_MESSAGE, CONSUMER_DOES_NOT_EXIST);
        }
    }

    /**
     * Handle an {@link ApiException} by returning 400 and the exception's message.
     *
     * @param apiException the exception to handle
     * @return the exception's message
     */
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel apiExceptionHandler(ApiException apiException) {
        return new ErrorModel(apiException.getMessage());
    }

    private static DestinationType toType(String type) {
        try {
            return DestinationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private static ApiException createError(String problem, String cause) {
        return new ApiException(problem + ": " + cause);
    }
}
