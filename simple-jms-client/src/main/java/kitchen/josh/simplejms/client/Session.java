package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.ConsumerId;
import kitchen.josh.simplejms.broker.Destination2;
import org.springframework.web.client.RestTemplate;

public class Session {

    private final String host;
    private final RestTemplate restTemplate;

    public Session(String host, RestTemplate restTemplate) {
        this.host = host;
        this.restTemplate = restTemplate;
    }

    public Producer createProducer(Destination2 destination) {
        return new Producer(destination, sendUrl(destination), restTemplate);
    }

    public Consumer createConsumer(Destination2 destination) {
        ConsumerId consumerId = restTemplate.postForEntity(createConsumerUrl(destination), null, ConsumerId.class).getBody();
        return new Consumer(destination, receiveUrl(destination, consumerId), restTemplate);
    }

    private String sendUrl(Destination2 destination) {
        return host + "/" + destinationUrl(destination) + "/send";
    }

    private String receiveUrl(Destination2 destination, ConsumerId consumerId) {
        return host + "/" + destinationUrl(destination) + "/receive/" + consumerId.getId();
    }

    private String createConsumerUrl(Destination2 destination) {
        return host + "/" + destinationUrl(destination) + "/consumer";
    }

    private static String destinationUrl(Destination2 destination) {
        return destination.getType().name().toLowerCase();
    }
}
