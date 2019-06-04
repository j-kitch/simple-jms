package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.BodyFactory;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.HeadersFactory;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.properties.Properties;
import kitchen.josh.simplejms.common.message.properties.PropertiesFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ConsumerIntegrationTest {

    private static final String HOST = "http://localhost:8080";

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, DESTINATION_ID);

    private static final UUID CONSUMER_ID = UUID.randomUUID();

    private static final String RECEIVE_URL = HOST + "/consumer/" + CONSUMER_ID + "/receive";
    private static final String ACKNOWLEDGE_URL = HOST + "/consumer/" + CONSUMER_ID + "/acknowledge";
    private static final String RECOVER_URL = HOST + "/consumer/" + CONSUMER_ID + "/recover";
    private static final String CLOSE_URL = HOST + "/consumer/" + CONSUMER_ID;

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
    public void acknowledge() {
        String json = "{\"id\": \"ID:1234\"}";
        Message message = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody());
        message.setId("ID:1234");

        mockRestServiceServer.expect(once(), requestTo(ACKNOWLEDGE_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(json))
                .andRespond(withSuccess());

        consumer.acknowledge(message);

        mockRestServiceServer.verify();
    }

    @Test
    public void recover() {
        mockRestServiceServer.expect(once(), requestTo(RECOVER_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess());

        consumer.recover();

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
