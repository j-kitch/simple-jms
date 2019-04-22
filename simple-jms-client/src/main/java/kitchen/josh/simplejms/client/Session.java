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
        return new Consumer(host, restTemplate, new ConsumerId(destination, consumerId.getId()));
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
