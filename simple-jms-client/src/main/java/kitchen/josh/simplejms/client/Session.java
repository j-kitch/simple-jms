package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.*;
import org.springframework.web.client.RestTemplate;

/**
 * This class implements the session for connecting to a SimpleJMS broker.
 */
public class Session {

    private final String host;
    private final RestTemplate restTemplate;

    /**
     * Create a new Session for a broker at the host
     *
     * @param host         the host to connect to
     * @param restTemplate a rest template to use
     */
    public Session(String host, RestTemplate restTemplate) {
        this.host = host;
        this.restTemplate = restTemplate;
    }

    /**
     * Create a new destination in the broker.
     * @param type the type of destination to create
     * @return the id of the destination created
     */
    public Destination createDestination(DestinationType type) {
        IdModel destinationId = restTemplate.postForEntity(createDestinationUrl(type), null, IdModel.class).getBody();
        return new Destination(type, destinationId.getId());
    }

    /**
     * Create a new producer for a destination.
     *
     * @param destination the destination to create a producer for
     * @return the created producer
     */
    public Producer createProducer(Destination destination) {
        IdModel producerId = restTemplate.postForEntity(createProducerUrl(destination), null, IdModel.class).getBody();
        return new Producer(host, restTemplate, new ProducerId(destination, producerId.getId()), new MessageModelFactory(new PropertyModelFactory(), new BodyModelFactory()));
    }

    /**
     * Create a consumer for a destination.
     * @param destination the destination to create a consumer for
     * @return the created consumer
     */
    public Consumer createConsumer(Destination destination) {
        IdModel consumerId = restTemplate.postForEntity(createConsumerUrl(destination), null, IdModel.class).getBody();
        return new Consumer(host, restTemplate, new ConsumerId(destination, consumerId.getId()), new MessageFactory(new PropertiesFactory(), new BodyFactory()));
    }

    public TextMessage createTextMessage() {
        return new TextMessage(new PropertiesImpl(), new TextBody());
    }

    public ObjectMessage createObjectMessage() {
        return new ObjectMessage(new PropertiesImpl(), new ObjectBody());
    }

    private String createConsumerUrl(Destination destination) {
        return host + "/" + destination.getType().name().toLowerCase() + "/" + destination.getId() + "/consumer";
    }

    private String createProducerUrl(Destination destination) {
        return host + "/" + destination.getType().name().toLowerCase() + "/" + destination.getId() + "/producer";
    }

    private String createDestinationUrl(DestinationType type) {
        return host + "/" + type.name().toLowerCase();
    }
}
