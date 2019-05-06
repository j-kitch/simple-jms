package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Message;
import kitchen.josh.simplejms.common.MessageModel;
import kitchen.josh.simplejms.common.PropertyModel;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.list;

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
    public void sendMessage(Message message) {
        String sendUrl = brokerUrl + "/" + id.getDestination().getType().name().toLowerCase() + "/" + id.getDestination().getId()
                + "/producer/" + id.getId() + "/send";

        List<PropertyModel> properties = list(message.getProperties().getPropertyNames()).stream()
                .map(name -> {
                    Object value = message.getProperties().getObjectProperty(name);
                    return new PropertyModel(name, value.getClass().getSimpleName(), value);
                })
                .collect(Collectors.toList());

        restTemplate.postForEntity(sendUrl, new MessageModel(properties, message.getMessage()), Void.class);
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
