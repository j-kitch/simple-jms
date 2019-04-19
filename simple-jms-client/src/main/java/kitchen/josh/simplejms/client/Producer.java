package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.MessageModel;
import org.springframework.web.client.RestTemplate;

public class Producer {

    private final String url;
    private final RestTemplate restTemplate;

    public Producer(String url, RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public void sendMessage(String message) {
        restTemplate.postForEntity(url, new MessageModel(message), Void.class);
    }
}
