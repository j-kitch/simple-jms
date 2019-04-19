package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.MessageModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class Consumer {

    private final String url;
    private final RestTemplate restTemplate;

    public Consumer(String url, RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public Optional<String> receiveMessage() {
        return Optional.ofNullable(restTemplate.postForEntity(url, null, MessageModel.class))
                .map(ResponseEntity::getBody)
                .map(MessageModel::getMessage);
    }
}
