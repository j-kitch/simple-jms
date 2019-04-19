package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.ConsumerId;
import org.springframework.web.client.RestTemplate;

public class Session {

    private final String host;
    private final RestTemplate restTemplate;

    public Session(String host, RestTemplate restTemplate) {
        this.host = host;
        this.restTemplate = restTemplate;
    }

    public Producer createProducer() {
        return new Producer(host + "/topic/send", restTemplate);
    }

    public Consumer createConsumer() {
        String createConsumerUrl = host + "/topic/consumer";
        ConsumerId consumerId = restTemplate.postForEntity(createConsumerUrl, null, ConsumerId.class).getBody();
        String consumerUrl = host + "/topic/receive/" + consumerId.getId();
        return new Consumer(consumerUrl, restTemplate);
    }
}
