package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Message;
import kitchen.josh.simplejms.broker.MessageModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class Consumer {

    private final String brokerUrl;
    private final RestTemplate restTemplate;
    private final ConsumerId id;

    public Consumer(String brokerUrl, RestTemplate restTemplate, ConsumerId id) {
        this.brokerUrl = brokerUrl;
        this.restTemplate = restTemplate;
        this.id = id;
    }

    public Optional<Message> receiveMessage() {
        String receiveUrl = brokerUrl + "/" + id.getDestination().getType().name().toLowerCase() + "/" + id.getDestination().getId() + "/consumer/"
                + id.getId() + "/receive";

        return Optional.ofNullable(restTemplate.postForEntity(receiveUrl, null, MessageModel.class))
                .map(ResponseEntity::getBody)
                .map(MessageModel::getMessage)
                .map(message -> new Message(id.getDestination(), message));
    }
}
