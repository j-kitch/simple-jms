package kitchen.josh.simplejms.integrationtests.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.Destination2;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.Session;
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

    private static final Destination2 QUEUE = new Destination2(Destination.QUEUE, null);
    private static final Destination2 TOPIC = new Destination2(Destination.TOPIC, null);
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
        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/consumer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\": \"" + consumerId + "\"}", MediaType.APPLICATION_JSON_UTF8));

        Session session = new Session(HOST, restTemplate);

        Consumer consumer = session.createConsumer(QUEUE);

        assertThat(consumer).isEqualToComparingFieldByField(
                new Consumer(QUEUE, HOST + "/queue/receive/" + consumerId, restTemplate));
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
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/consumer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\": \"" + consumerId + "\"}", MediaType.APPLICATION_JSON_UTF8));

        Session session = new Session(HOST, restTemplate);

        Consumer consumer = session.createConsumer(TOPIC);

        assertThat(consumer).isEqualToComparingFieldByField(
                new Consumer(TOPIC, HOST + "/topic/receive/" + consumerId, restTemplate));
        mockRestServiceServer.verify();
    }

    @Test
    public void createTopicProducer() {
        Session session = new Session(HOST, restTemplate);

        Producer producer = session.createProducer(TOPIC);

        assertThat(producer).isEqualToComparingFieldByField(
                new Producer(TOPIC, HOST + "/topic/send", restTemplate));
    }

    @Test
    public void createQueueProducer() {
        Session session = new Session(HOST, restTemplate);

        Producer producer = session.createProducer(QUEUE);

        assertThat(producer).isEqualToComparingFieldByField(
                new Producer(QUEUE, HOST + "/queue/send", restTemplate));
    }
}
