package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.ConsumerId;
import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
import org.springframework.web.client.RestTemplate;

public class Session {

    private final String host;
    private final RestTemplate restTemplate;

    public Session(String host, RestTemplate restTemplate) {
        this.host = host;
        this.restTemplate = restTemplate;
    }

    public Destination createDestination(DestinationType type) {
        ConsumerId destinationId = restTemplate.postForEntity(host + "/" + destinationUrl(new Destination(type, null)), null, ConsumerId.class).getBody();
        return new Destination(type, destinationId.getId());
    }

    public Producer createProducer(Destination destination) {
        return new Producer(destination, sendUrl(destination), restTemplate);
    }

    public Consumer createConsumer(Destination destination) {
        ConsumerId consumerId = restTemplate.postForEntity(createConsumerUrl(destination), null, ConsumerId.class).getBody();
        return new Consumer(destination, receiveUrl(destination, consumerId), restTemplate);
    }

    private String sendUrl(Destination destination) {
        return host + "/" + destinationUrl(destination) + "/send";
    }

    private String receiveUrl(Destination destination, ConsumerId consumerId) {
        return host + "/" + destinationUrl(destination) + "/receive/" + consumerId.getId();
    }

    private String createConsumerUrl(Destination destination) {
        return host + "/" + destinationUrl(destination) + "/consumer";
    }

    private static String destinationUrl(Destination destination) {
        return destination.getType().name().toLowerCase();
    }
}
