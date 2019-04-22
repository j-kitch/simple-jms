package kitchen.josh.simplejms.integrationtests.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.Message;
import kitchen.josh.simplejms.client.Consumer;
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

public class ConsumerTest {

    private static final String HOST = "http://localhost:8080";

    private RestTemplate restTemplate;
    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void receiveMessageFromQueue_noMessage_returnsEmpty() {
        UUID consumerId = UUID.randomUUID();
        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/receive/" + consumerId))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"message\": null}", MediaType.APPLICATION_JSON_UTF8));
        Consumer consumer = new Consumer(Destination.QUEUE, HOST + "/queue/receive/" + consumerId, restTemplate);

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).isEmpty();
        mockRestServiceServer.verify();
    }

    @Test
    public void receiveMessageFromQueue_message_returnsMessage() {
        UUID consumerId = UUID.randomUUID();
        String message = "hello world";
        mockRestServiceServer.expect(once(), requestTo(HOST + "/queue/receive/" + consumerId))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"message\": \"" + message + "\"}", MediaType.APPLICATION_JSON_UTF8));
        Consumer consumer = new Consumer(Destination.QUEUE, HOST + "/queue/receive/" + consumerId, restTemplate);

        Optional<Message> received = consumer.receiveMessage();

        assertThat(received).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, message));
        mockRestServiceServer.verify();
    }

    @Test
    public void receiveMessageFromTopic_noMessage_returnsEmpty() {
        UUID consumerId = UUID.randomUUID();
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/receive/" + consumerId))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"message\": null}", MediaType.APPLICATION_JSON_UTF8));
        Consumer consumer = new Consumer(Destination.TOPIC, HOST + "/topic/receive/" + consumerId, restTemplate);

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).isEmpty();
        mockRestServiceServer.verify();
    }

    @Test
    public void receiveMessageFromTopic_message_returnsMessage() {
        UUID consumerId = UUID.randomUUID();
        String message = "hello world";
        mockRestServiceServer.expect(once(), requestTo(HOST + "/topic/receive/" + consumerId))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"message\": \"" + message + "\"}", MediaType.APPLICATION_JSON_UTF8));
        Consumer consumer = new Consumer(Destination.TOPIC, HOST + "/topic/receive/" + consumerId, restTemplate);

        Optional<Message> received = consumer.receiveMessage();

        assertThat(received).usingFieldByFieldValueComparator().contains(new Message(Destination.TOPIC, message));
        mockRestServiceServer.verify();
    }
}
