package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.Message;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ConsumerIntegrationTest {

    private static final String HOST = "http://localhost:8080";
    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final String MESSAGE = "hello world";

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
    public void receiveMessage_noMessage_returnsEmpty() {
        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"message\": null}", MediaType.APPLICATION_JSON_UTF8));
        Consumer consumer = new Consumer(HOST, restTemplate, new ConsumerId(QUEUE, CONSUMER_ID));

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).isEmpty();
        mockRestServiceServer.verify();
    }

    @Test
    public void receiveMessage_message_returnsMessage() {
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"message\": \"" + MESSAGE + "\"}", MediaType.APPLICATION_JSON_UTF8));
        Consumer consumer = new Consumer(HOST, restTemplate, new ConsumerId(TOPIC, CONSUMER_ID));

        Optional<Message> received = consumer.receiveMessage();

        assertThat(received).usingFieldByFieldValueComparator().contains(new Message(TOPIC, MESSAGE));
        mockRestServiceServer.verify();
    }

    @Test
    public void close() {
        Consumer consumer = new Consumer(HOST, restTemplate, new ConsumerId(TOPIC, CONSUMER_ID));
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        consumer.close();

        mockRestServiceServer.verify();
    }
}
