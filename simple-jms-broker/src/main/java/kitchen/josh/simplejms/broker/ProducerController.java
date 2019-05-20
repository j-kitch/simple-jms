package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.DestinationModel;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.ErrorModel;
import kitchen.josh.simplejms.common.IdModel;
import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.MessageModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
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

    private final ProducerService producerService;
    private final MessageFactory messageFactory;

    public ProducerController(ProducerService producerService, MessageFactory messageFactory) {
        this.producerService = producerService;
        this.messageFactory = messageFactory;
    }

    /**
     * Create a new producer for a destination.
     *
     * @param model the destination to create a producer for
     * @return the id of the created consumer
     */
    @PostMapping(path = "/producer")
    public IdModel createProducer(@RequestBody DestinationModel model) {
        try {
            return new IdModel(producerService.createProducer(model.getDestination()));
        } catch (DestinationDoesNotExistException e) {
            throw createError(FAILED_CREATE_PRODUCER, DESTINATION_DOES_NOT_EXIST);
        }
    }

    /**
     * Delete a producer.
     *
     * @param producerId      the id of the producer
     */
    @DeleteMapping(path = "/producer/{producerId}")
    public void deleteProducer(@PathVariable UUID producerId) {
        try {
            producerService.removeProducer(producerId);
        } catch (ProducerDoesNotExistException e) {
            throw createError(FAILED_DELETE_PRODUCER, PRODUCER_DOES_NOT_EXIST);
        }
    }

    /**
     * Send a message from a producer to a destination.
     *
     * @param producerId the id of the producer
     * @param message    the message to send to the destination
     */
    @PostMapping(path = "/producer/{producerId}/send")
    public void sendMessage(@PathVariable UUID producerId, @RequestBody MessageModel message) throws MessageFormatException {
        try {
            producerService.sendMessage(producerId, messageFactory.create(message));
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

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel malformedJsonHandler() {
        return new ErrorModel("Malformed JSON");
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
