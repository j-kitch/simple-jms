package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.DestinationModel;
import kitchen.josh.simplejms.common.ErrorModel;
import kitchen.josh.simplejms.common.IdModel;
import kitchen.josh.simplejms.common.message.MessageIdModel;
import kitchen.josh.simplejms.common.message.MessageModel;
import kitchen.josh.simplejms.common.message.MessageModelFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static java.util.Collections.emptyList;

@RestController
public class ConsumerController {

    private static final String FAILED_CREATE_CONSUMER = "Failed to create consumer";
    private static final String FAILED_DELETE_CONSUMER = "Failed to delete consumer";
    private static final String FAILED_RECEIVE_MESSAGE = "Failed to receive message";
    private static final String FAILED_ACKNOWLEDGE_MESSAGE = "Failed to acknowledge message";
    private static final String FAILED_RECOVER_CONSUMER = "Failed to recover consumer";

    private static final String DESTINATION_DOES_NOT_EXIST = "the destination does not exist";
    private static final String CONSUMER_DOES_NOT_EXIST = "the consumer does not exist";

    private final ConsumerManager consumerManager;
    private final MessageModelFactory messageModelFactory;

    public ConsumerController(ConsumerManager consumerManager, MessageModelFactory messageModelFactory) {
        this.consumerManager = consumerManager;
        this.messageModelFactory = messageModelFactory;
    }

    /**
     * Create a new consumer for a destination.
     *
     * @param model the destination to create the consumer for
     * @return the id of the created consumer
     */
    @PostMapping(path = "/consumer")
    public IdModel createConsumer(@RequestBody DestinationModel model) {
        try {
            return new IdModel(consumerManager.createConsumer(model.getDestination()));
        } catch (DestinationDoesNotExistException e) {
            throw createError(FAILED_CREATE_CONSUMER, DESTINATION_DOES_NOT_EXIST);
        }
    }

    /**
     * Delete a consumer.
     *
     * @param consumerId the id of the consumer
     */
    @DeleteMapping(path = "/consumer/{consumerId}")
    public void deleteConsumer(@PathVariable UUID consumerId) {
        try {
            consumerManager.removeConsumer(consumerId);
        } catch (ConsumerDoesNotExistException e) {
            throw createError(FAILED_DELETE_CONSUMER, CONSUMER_DOES_NOT_EXIST);
        }
    }

    /**
     * Receive a message for a consumer from a destination.
     *
     * @param consumerId the id of the consumer
     * @return the message received from the destination
     */
    @PostMapping(path = "/consumer/{consumerId}/receive")
    public MessageModel receiveMessage(@PathVariable UUID consumerId) {
        return consumerManager.findConsumer(consumerId)
                .orElseThrow(() -> createError(FAILED_RECEIVE_MESSAGE, CONSUMER_DOES_NOT_EXIST))
                .receive()
                .map(messageModelFactory::create)
                .orElse(new MessageModel(null, emptyList(), null));
    }

    @PostMapping(path = "/consumer/{consumerId}/acknowledge")
    public void acknowledge(@PathVariable UUID consumerId, @RequestBody MessageIdModel messageIdModel) {
        consumerManager.findConsumer(consumerId)
                .orElseThrow(() -> createError(FAILED_ACKNOWLEDGE_MESSAGE, CONSUMER_DOES_NOT_EXIST))
                .acknowledge(messageIdModel.getId());
    }

    @PostMapping(path = "/consumer/{consumerId}/recover")
    public void recover(@PathVariable UUID consumerId) {
        consumerManager.findConsumer(consumerId)
                .orElseThrow(() -> createError(FAILED_RECOVER_CONSUMER, CONSUMER_DOES_NOT_EXIST))
                .recover();
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

    private static ApiException createError(String problem, String cause) {
        return new ApiException(problem + ": " + cause);
    }
}
