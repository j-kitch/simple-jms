package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.ErrorModel;
import kitchen.josh.simplejms.common.IdModel;
import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.MessageModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.jms.MessageFormatException;
import java.util.UUID;

@RestController
public class ProducerController {

    private static final String FAILED_CREATE_PRODUCER = "Failed to create producer";
    private static final String FAILED_DELETE_PRODUCER = "Failed to delete producer";
    private static final String FAILED_SEND_MESSAGE = "Failed to send message";

    private static final String DESTINATION_DOES_NOT_EXIST = "the destination does not exist";
    private static final String PRODUCER_DOES_NOT_EXIST = "the producer does not exist";

    private final DestinationService destinationService;
    private final MessageFactory messageFactory;

    public ProducerController(DestinationService destinationService, MessageFactory messageFactory) {
        this.destinationService = destinationService;
        this.messageFactory = messageFactory;
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
