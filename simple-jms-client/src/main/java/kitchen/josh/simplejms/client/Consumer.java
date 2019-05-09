package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Message;
import kitchen.josh.simplejms.common.MessageModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.jms.MessageFormatException;
import java.util.Optional;

/**
 * A consumer for a destination, used for receiving messages.
 */
public class Consumer implements AutoCloseable {

    private final String brokerUrl;
    private final RestTemplate restTemplate;
    private final ConsumerId id;

    public Consumer(String brokerUrl, RestTemplate restTemplate, ConsumerId id) {
        this.brokerUrl = brokerUrl;
        this.restTemplate = restTemplate;
        this.id = id;
    }

    /**
     * Receive a message from the consumer's destination.
     *
     * @return the next message for the consumer, or <code>Optional.empty()</code> if there isn't a message
     */
    public Optional<Message> receiveMessage() {
        String receiveUrl = brokerUrl + "/" + id.getDestination().getType().name().toLowerCase() + "/" + id.getDestination().getId() + "/consumer/"
                + id.getId() + "/receive";

        return Optional.ofNullable(restTemplate.postForEntity(receiveUrl, null, MessageModel.class))
                .map(ResponseEntity::getBody)
                .filter(model -> model.getMessage() != null)
                .map(model -> {
                    Message message = new Message(id.getDestination(), model.getMessage());
                    model.getProperties().forEach(property -> {
                        try {
                            message.getProperties().setObjectProperty(property.getName(), property.getValue());
                        } catch (MessageFormatException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    return message;
                });
    }

    /**
     * Close the consumer, telling the broker to remove resources allocated for this consumer.
     */
    @Override
    public void close() {
        String deleteUrl = brokerUrl + "/" + id.getDestination().getType().name().toLowerCase() + "/" + id.getDestination().getId() + "/consumer/"
                + id.getId();

        restTemplate.delete(deleteUrl);
    }
}
