package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.MessageModelFactory;
import org.springframework.web.client.RestTemplate;

/**
 * A producer for sending messages to a broker's destination.
 */
public class Producer implements AutoCloseable {

    private final String brokerUrl;
    private final RestTemplate restTemplate;
    private final ProducerId id;
    private final MessageModelFactory messageModelFactory;

    public Producer(String brokerUrl, RestTemplate restTemplate, ProducerId id, MessageModelFactory messageModelFactory) {
        this.brokerUrl = brokerUrl;
        this.restTemplate = restTemplate;
        this.id = id;
        this.messageModelFactory = messageModelFactory;
    }

    /**
     * Send a message to the producer's destination.
     *
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        String sendUrl = brokerUrl + "/producer/" + id.getId() + "/send";

        restTemplate.postForEntity(sendUrl, messageModelFactory.create(message), Void.class);
    }

    /**
     * Get the id of the producer.
     *
     * @return the id of the producer
     */
    public ProducerId getId() {
        return id;
    }

    /**
     * Close the producer, telling the broker that this producer no longer exists.
     */
    @Override
    public void close() {
        String deleteUrl = brokerUrl + "/producer/" + id.getId();

        restTemplate.delete(deleteUrl);
    }
}
