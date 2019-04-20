package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.Message;
import kitchen.josh.simplejms.broker.MessageModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class Consumer {

    private final Destination destination;
    private final String url;
    private final RestTemplate restTemplate;

    public Consumer(Destination destination, String url, RestTemplate restTemplate) {
        this.destination = destination;
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public Destination getDestination() {
        return destination;
    }

    public Optional<Message> receiveMessage() {
        return Optional.ofNullable(restTemplate.postForEntity(url, null, MessageModel.class))
                .map(ResponseEntity::getBody)
                .map(MessageModel::getMessage)
                .map(message -> new Message(Destination.TOPIC, message));
    }
}
