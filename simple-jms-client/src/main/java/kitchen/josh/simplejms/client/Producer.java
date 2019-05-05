package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.MessageModel;
import org.springframework.web.client.RestTemplate;

/**
 * A producer for sending messages to a broker's destination.
 */
public class Producer implements AutoCloseable {

    private final String brokerUrl;
    private final RestTemplate restTemplate;
    private final ProducerId id;

    public Producer(String brokerUrl, RestTemplate restTemplate, ProducerId id) {
        this.brokerUrl = brokerUrl;
        this.restTemplate = restTemplate;
        this.id = id;
    }

    /**
     * Send a message to the producer's destination.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        String sendUrl = brokerUrl + "/" + id.getDestination().getType().name().toLowerCase() + "/" + id.getDestination().getId()
                + "/producer/" + id.getId() + "/send";
        restTemplate.postForEntity(sendUrl, new MessageModel(message), Void.class);
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
        String deleteUrl = brokerUrl + "/" + id.getDestination().getType().name().toLowerCase() + "/" + id.getDestination().getId()
                + "/producer/" + id.getId();

        restTemplate.delete(deleteUrl);
    }
}
