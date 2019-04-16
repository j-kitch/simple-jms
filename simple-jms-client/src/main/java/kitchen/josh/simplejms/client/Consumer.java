package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

public class Consumer {

    private final String url;
    private final RestTemplate restTemplate;

    public Consumer(String host, UUID consumerId, RestTemplate restTemplate) {
        this.url = host + "/consumer/" + consumerId;
        this.restTemplate = restTemplate;
    }

    public Optional<String> receiveMessage() {
        return Optional.ofNullable(restTemplate.postForEntity(url, null, Message.class))
                .map(ResponseEntity::getBody)
                .map(Message::getMessage);
    }
}
