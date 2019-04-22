package kitchen.josh.simplejms.integrationtests.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
import kitchen.josh.simplejms.client.Producer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Integration tests for the Client's Producer class.
 */
public class ProducerTest {

    private static final String HOST = "http://localhost:8080";

    private RestTemplate restTemplate;
    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void sendMessageToQueue() {
        String message = "hello world";
        Producer producer = new Producer(new Destination(DestinationType.QUEUE, null), HOST + "/queue/send", restTemplate);

        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/send"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + message + "\"}"))
                .andRespond(withSuccess());

        producer.sendMessage(message);

        mockRestServiceServer.verify();
    }

    @Test
    public void sendMessageToTopic() {
        String message = "hello world";
        Producer producer = new Producer(new Destination(DestinationType.TOPIC, null), HOST + "/topic/send", restTemplate);

        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/send"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + message + "\"}"))
                .andRespond(withSuccess());

        producer.sendMessage(message);

        mockRestServiceServer.verify();
    }
}
