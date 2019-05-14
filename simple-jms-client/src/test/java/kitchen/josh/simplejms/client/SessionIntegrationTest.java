package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.*;
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

public class SessionIntegrationTest {

    private static final String HOST = "http://localhost:8080";
    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();
    private static final Destination QUEUE = new Destination(DestinationType.QUEUE, DESTINATION_ID);
    private static final Destination TOPIC = new Destination(DestinationType.TOPIC, DESTINATION_ID);

    private RestTemplate restTemplate;
    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void createConsumer() {
        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/" + DESTINATION_ID + "/consumer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\": \"" + ID + "\"}", MediaType.APPLICATION_JSON_UTF8));

        Session session = new Session(HOST, restTemplate);

        Consumer consumer = session.createConsumer(QUEUE);

        assertThat(consumer).isEqualToComparingFieldByFieldRecursively(
                new Consumer(HOST, restTemplate, new ConsumerId(QUEUE, ID), new MessageFactory(new PropertiesFactory())));
        mockRestServiceServer.verify();
    }

    @Test
    public void createProducer() {
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/" + DESTINATION_ID + "/producer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\": \"" + ID + "\"}", MediaType.APPLICATION_JSON_UTF8));
        Session session = new Session(HOST, restTemplate);

        Producer producer = session.createProducer(TOPIC);

        assertThat(producer).isEqualToComparingFieldByFieldRecursively(
                new Producer(HOST, restTemplate, new ProducerId(TOPIC, ID), new MessageModelFactory(new PropertyModelFactory())));
        mockRestServiceServer.verify();
    }
}
