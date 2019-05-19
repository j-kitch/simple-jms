package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.*;
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
    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, DESTINATION_ID);

    private static final UUID CONSUMER_ID = UUID.randomUUID();

    private static final String RECEIVE_URL = HOST + "/queue/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive";
    private static final String CLOSE_URL = HOST + "/queue/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID;

    private static final String TEXT = "hello world";

    private static final MessageFactory MESSAGE_FACTORY = new MessageFactory(new HeadersFactory(), new PropertiesFactory(), new BodyFactory());

    private MockRestServiceServer mockRestServiceServer;

    private Consumer consumer;

    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
        consumer = new Consumer(HOST, restTemplate, new ConsumerId(DESTINATION, CONSUMER_ID), MESSAGE_FACTORY);
    }

    @Test
    public void receiveMessage_noMessage_returnsEmpty() {
        mockRestServiceServer.expect(once(), requestTo(RECEIVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"body\": null, \"properties\": []}", MediaType.APPLICATION_JSON_UTF8));

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).isEmpty();
        mockRestServiceServer.verify();
    }

    @Test
    public void receiveMessage_message_returnsMessage() {
        String json = "{\"body\": {\"type\": \"text\", \"text\": \"" + TEXT + "\"}, \"properties\": [" +
                "{\"name\": \"property 1\", \"type\": \"Float\", \"value\": 1.2}," +
                "{\"name\": \"property 2\", \"type\": \"String\", \"value\": \"other property\"}]," +
                "\"headers\": {\"JMSMessageID\": \"ID:1234\", \"JMSDestination\": \"topic:" + DESTINATION_ID + "\"}}";
        Properties properties = new PropertiesImpl();
        properties.setFloatProperty("property 1", 1.2f);
        properties.setStringProperty("property 2", "other property");

        mockRestServiceServer.expect(once(), requestTo(RECEIVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON_UTF8));

        Optional<Message> received = consumer.receiveMessage();

        Message expected = new TextMessage(new HeadersImpl(), properties, new TextBody(TEXT));
        expected.setId("ID:1234");
        expected.setDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        assertThat(received.get()).isEqualToComparingFieldByFieldRecursively(expected);
        mockRestServiceServer.verify();
    }

    @Test
    public void close() {
        mockRestServiceServer.expect(once(), requestTo(CLOSE_URL))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        consumer.close();

        mockRestServiceServer.verify();
    }
}
