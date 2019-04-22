package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
import kitchen.josh.simplejms.broker.IdModel;
import org.springframework.web.client.RestTemplate;

public class Session {

    private final String host;
    private final RestTemplate restTemplate;

    public Session(String host, RestTemplate restTemplate) {
        this.host = host;
        this.restTemplate = restTemplate;
    }

    public Destination createDestination(DestinationType type) {
        IdModel destinationId = restTemplate.postForEntity(createDestinationUrl(type), null, IdModel.class).getBody();
        return new Destination(type, destinationId.getId());
    }

    public Producer createProducer(Destination destination) {
        IdModel producerId = restTemplate.postForEntity(createProducerUrl(destination), null, IdModel.class).getBody();
        return new Producer(host, restTemplate, new ProducerId(destination, producerId.getId()));
    }

    public Consumer createConsumer(Destination destination) {
        IdModel consumerId = restTemplate.postForEntity(createConsumerUrl(destination), null, IdModel.class).getBody();
        return new Consumer(destination, receiveUrl(destination, consumerId), restTemplate);
    }

    private String sendUrl(Destination destination) {
        return host + "/" + destinationUrl(destination) + "/send";
    }

    private String receiveUrl(Destination destination, IdModel consumerId) {
        return host + "/" + destinationUrl(destination) + "/receive/" + consumerId.getId();
    }

    private String createConsumerUrl(Destination destination) {
        return host + "/" + destinationUrl(destination) + "/consumer";
    }

    private String createDestinationUrl(DestinationType type) {
        return host + "/" + destinationUrl(type);
    }

    private String createProducerUrl(Destination destination) {
        return host + "/" + destinationUrl(destination.getType()) + "/" + destination.getId() + "/producer";
    }

    private static String destinationUrl(Destination destination) {
        return destinationUrl(destination.getType());
    }

    private static String destinationUrl(DestinationType type) {
        return type.name().toLowerCase();
    }
}
