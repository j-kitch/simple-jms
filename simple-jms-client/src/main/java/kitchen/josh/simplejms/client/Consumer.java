package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.MessageIdModel;
import kitchen.josh.simplejms.common.message.MessageModel;
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
    private final MessageFactory messageFactory;

    public Consumer(String brokerUrl, RestTemplate restTemplate, ConsumerId id, MessageFactory messageFactory) {
        this.brokerUrl = brokerUrl;
        this.restTemplate = restTemplate;
        this.id = id;
        this.messageFactory = messageFactory;
    }

    /**
     * Receive a message from the consumer's destination.
     *
     * @return the next message for the consumer, or <code>Optional.empty()</code> if there isn't a message
     */
    public Optional<Message> receiveMessage() {
        String receiveUrl = brokerUrl + "/consumer/" + id.getId() + "/receive";

        return Optional.ofNullable(restTemplate.postForEntity(receiveUrl, null, MessageModel.class))
                .map(ResponseEntity::getBody)
                .map(this::createMessage);
    }

    public void acknowledge(Message message) {
        String acknowledgeUrl = brokerUrl + "/consumer/" + id.getId() + "/acknowledge";
        restTemplate.postForEntity(acknowledgeUrl, new MessageIdModel(message.getId()), Void.class);
    }

    public void recover() {
        String recoverUrl = brokerUrl + "/consumer/" + id.getId() + "/recover";
        restTemplate.postForEntity(recoverUrl, null, Void.class);
    }

    /**
     * Close the consumer, telling the broker to remove resources allocated for this consumer.
     */
    @Override
    public void close() {
        String deleteUrl = brokerUrl + "/consumer/" + id.getId();

        restTemplate.delete(deleteUrl);
    }

    private Message createMessage(MessageModel model) {
        try {
            return messageFactory.create(model);
        } catch (MessageFormatException mfe) {
            throw new RuntimeException(mfe);
        }
    }
}
