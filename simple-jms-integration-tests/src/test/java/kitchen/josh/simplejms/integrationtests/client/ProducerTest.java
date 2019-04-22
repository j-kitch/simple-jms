package kitchen.josh.simplejms.integrationtests.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.ProducerId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Integration tests for the Client's Producer class.
 */
public class ProducerTest {

    private static final String HOST = "http://localhost:8080";
    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final UUID PRODUCER_ID = UUID.randomUUID();

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
        Producer producer = new Producer(HOST, restTemplate, new ProducerId(new Destination(DestinationType.QUEUE, DESTINATION_ID), PRODUCER_ID));

        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_ID + "/send"))
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
        Producer producer = new Producer(HOST, restTemplate, new ProducerId(new Destination(DestinationType.TOPIC, DESTINATION_ID), PRODUCER_ID));

        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/" + DESTINATION_ID + "/producer/" + PRODUCER_ID + "/send"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + message + "\"}"))
                .andRespond(withSuccess());

        producer.sendMessage(message);

        mockRestServiceServer.verify();
    }

    @Test
    public void close_notifiesBroker() {
        Producer producer = new Producer(HOST, restTemplate, new ProducerId(new Destination(DestinationType.TOPIC, DESTINATION_ID), PRODUCER_ID));
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/" + DESTINATION_ID + "/producer/" + PRODUCER_ID))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        producer.close();

        mockRestServiceServer.verify();
    }
}
