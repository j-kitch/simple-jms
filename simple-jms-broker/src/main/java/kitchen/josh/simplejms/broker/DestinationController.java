package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static java.util.Collections.emptyList;

/**
 * The controller for the broker's destinations.  With an API for creating destinations, producers and consumers,
 * and sending and receiving messages.
 */
@RestController
public class DestinationController {

    private final DestinationService destinationService;
    private final MessageModelFactory messageModelFactory;

    public DestinationController(DestinationService destinationService, MessageModelFactory messageModelFactory) {
        this.destinationService = destinationService;
        this.messageModelFactory = messageModelFactory;
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
        UUID consumerId = destinationService.findDestination(toType(destinationType), destinationId)
                .map(SingleDestinationService::createConsumer)
                .orElseThrow(() -> new ApiException("Failed to create consumer for " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."));
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
        UUID consumerId = destinationService.findDestination(toType(destinationType), destinationId)
                .map(SingleDestinationService::createProducer)
                .orElseThrow(() -> new ApiException("Failed to create producer for " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."));
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
            destinationService.findDestination(toType(destinationType), destinationId)
                    .orElseThrow(() -> new ApiException("Failed to delete consumer " + consumerId + " for " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."))
                    .removeConsumer(consumerId);
        } catch (ConsumerDoesNotExistException e) {
            throw new ApiException("Failed to delete consumer " + consumerId + " for " + destinationType + " " + destinationId + ": the consumer does not exist.");
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
            destinationService.findDestination(toType(destinationType), destinationId)
                    .orElseThrow(() -> new ApiException("Failed to delete producer " + producerId + " for " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."))
                    .removeProducer(producerId);
        } catch (ProducerDoesNotExistException e) {
            throw new ApiException("Failed to delete producer " + producerId + " for " + destinationType + " " + destinationId + ": the producer does not exist.");
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
                            @PathVariable UUID producerId, @RequestBody MessageModel message) {
        try {
            destinationService.findDestination(toType(destinationType), destinationId)
                    .orElseThrow(() -> new ApiException("Failed to send message to " + destinationType + " " + destinationId + ": the " + destinationType + " does not exist."))
                    .addMessage(producerId, new Message(new Destination(toType(destinationType), destinationId), message.getMessage()));
        } catch (ProducerDoesNotExistException e) {
            throw new ApiException("Failed to send message to " + destinationType + " " + destinationId + ": the producer " + producerId + " does not exist.");
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
            return destinationService.findDestination(toType(destinationType), destinationId)
                    .orElseThrow(() -> new ApiException("Failed to receive message: the " + destinationType + " " + destinationId + " does not exist."))
                    .readMessage(consumerId)
                    .map(messageModelFactory::create)
                    .orElse(new MessageModel(emptyList(), null));
        } catch (ConsumerDoesNotExistException e) {
            throw new ApiException("Failed to receive message: the consumer " + consumerId + " does not exist.");
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
}
