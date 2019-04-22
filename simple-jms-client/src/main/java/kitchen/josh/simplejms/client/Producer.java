package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.MessageModel;
import org.springframework.web.client.RestTemplate;

public class Producer implements AutoCloseable {

    private final String brokerUrl;
    private final RestTemplate restTemplate;
    private final ProducerId id;

    public Producer(String brokerUrl, RestTemplate restTemplate, ProducerId id) {
        this.brokerUrl = brokerUrl;
        this.restTemplate = restTemplate;
        this.id = id;
    }

    public void sendMessage(String message) {
        String sendUrl = brokerUrl + "/" + id.getDestination().getType().name().toLowerCase() + "/" + id.getDestination().getId()
                + "/producer/" + id.getId() + "/send";
        restTemplate.postForEntity(sendUrl, new MessageModel(message), Void.class);
    }

    public ProducerId getId() {
        return id;
    }

    @Override
    public void close() {
        String deleteUrl = brokerUrl + "/" + id.getDestination().getType().name().toLowerCase() + "/" + id.getDestination().getId()
                + "/producer/" + id.getId();

        restTemplate.delete(deleteUrl);
    }
}
