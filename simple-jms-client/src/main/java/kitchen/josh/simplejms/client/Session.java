package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationModel;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.IdModel;
import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.MessageModelFactory;
import kitchen.josh.simplejms.common.message.ObjectMessage;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.BodyFactory;
import kitchen.josh.simplejms.common.message.body.BodyModelFactory;
import kitchen.josh.simplejms.common.message.body.ObjectBody;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.HeadersFactory;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.headers.HeadersModelFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import kitchen.josh.simplejms.common.message.properties.PropertyModelFactory;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

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
        return new Producer(host, restTemplate, new ProducerId(destination, producerId.getId()), new MessageModelFactory(new HeadersModelFactory(), new PropertyModelFactory(), new BodyModelFactory()));
    }

    /**
     * Create a consumer for a destination.
     * @param destination the destination to create a consumer for
     * @return the created consumer
     */
    public Consumer createConsumer(Destination destination) {
        IdModel consumerId = restTemplate.postForEntity(host + "/consumer", new DestinationModel(destination), IdModel.class).getBody();
        return new Consumer(host, restTemplate, new ConsumerId(destination, consumerId.getId()), new MessageFactory(new HeadersFactory(), new PropertiesFactory(), new BodyFactory()));
    }

    public TextMessage createTextMessage() {
        return new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody());
    }

    public TextMessage createTextMessage(String text) {
        TextMessage textMessage = createTextMessage();
        textMessage.setText(text);
        return textMessage;
    }

    public ObjectMessage createObjectMessage() {
        return new ObjectMessage(new HeadersImpl(), new PropertiesImpl(), new ObjectBody());
    }

    public ObjectMessage createObjectMessage(Serializable serializable) {
        ObjectMessage objectMessage = createObjectMessage();
        objectMessage.setObject(serializable);
        return objectMessage;
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
