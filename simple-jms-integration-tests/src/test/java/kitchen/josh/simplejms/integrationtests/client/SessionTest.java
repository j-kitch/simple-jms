package kitchen.josh.simplejms.integrationtests.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
import kitchen.josh.simplejms.client.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Integration tests for the Client's Session class.
 */
public class SessionTest {

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final Destination QUEUE = new Destination(DestinationType.QUEUE, DESTINATION_ID);
    private static final Destination TOPIC = new Destination(DestinationType.TOPIC, DESTINATION_ID);
    private static final String HOST = "http://localhost:8080";

    private RestTemplate restTemplate;
    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    /**
     * Given a broker that responds with a new consumer ID,
     * When a Session creates a queue consumer,
     * Then it constructs a new queue consumer with that ID.
     */
    @Test
    public void createQueueConsumer_callsBrokerAndUsesId() {
        UUID consumerId = UUID.randomUUID();
        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/" + DESTINATION_ID + "/consumer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\": \"" + consumerId + "\"}", MediaType.APPLICATION_JSON_UTF8));

        Session session = new Session(HOST, restTemplate);

        Consumer consumer = session.createConsumer(QUEUE);

        assertThat(consumer).isEqualToComparingFieldByFieldRecursively(
                new Consumer(HOST, restTemplate, new ConsumerId(QUEUE, consumerId)));
        mockRestServiceServer.verify();
    }

    /**
     * Given a broker that responds with a new consumer ID,
     * When a Session creates a topic consumer,
     * Then it constructs a new topic consumer with that ID.
     */
    @Test
    public void createTopicConsumer_callsBrokerAndUsesId() {
        UUID consumerId = UUID.randomUUID();
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/" + DESTINATION_ID + "/consumer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\": \"" + consumerId + "\"}", MediaType.APPLICATION_JSON_UTF8));

        Session session = new Session(HOST, restTemplate);

        Consumer consumer = session.createConsumer(TOPIC);

        assertThat(consumer).isEqualToComparingFieldByFieldRecursively(
                new Consumer(HOST, restTemplate, new ConsumerId(TOPIC, consumerId)));
        mockRestServiceServer.verify();
    }

    @Test
    public void createTopicProducer() {
        UUID producerId = UUID.randomUUID();
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/" + DESTINATION_ID + "/producer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\": \"" + producerId + "\"}", MediaType.APPLICATION_JSON_UTF8));
        Session session = new Session(HOST, restTemplate);

        Producer producer = session.createProducer(TOPIC);

        assertThat(producer).isEqualToComparingFieldByFieldRecursively(
                new Producer(HOST, restTemplate, new ProducerId(TOPIC, producerId)));
        mockRestServiceServer.verify();
    }

    @Test
    public void createQueueProducer() {
        UUID producerId = UUID.randomUUID();
        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/" + DESTINATION_ID + "/producer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\": \"" + producerId + "\"}", MediaType.APPLICATION_JSON_UTF8));
        Session session = new Session(HOST, restTemplate);

        Producer producer = session.createProducer(QUEUE);

        assertThat(producer).isEqualToComparingFieldByFieldRecursively(
                new Producer(HOST, restTemplate, new ProducerId(QUEUE, producerId)));
        mockRestServiceServer.verify();
    }
}
