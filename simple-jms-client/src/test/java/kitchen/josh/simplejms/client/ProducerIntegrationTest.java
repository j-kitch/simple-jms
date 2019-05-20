package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.MessageModelFactory;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.BodyModelFactory;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.headers.HeadersModelFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import kitchen.josh.simplejms.common.message.properties.PropertyModelFactory;
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

public class ProducerIntegrationTest {

    private static final String HOST = "http://localhost:8080";
    private static final String TEXT = "hello world";

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, DESTINATION_ID);

    private static final UUID PRODUCER_UUID = UUID.randomUUID();
    private static final ProducerId PRODUCER_ID = new ProducerId(DESTINATION, PRODUCER_UUID);

    private static final String SEND_URL = HOST + "/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_UUID + "/send";
    private static final String CLOSE_URL = HOST + "/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_UUID;

    private static final String JSON = "{\"body\": {\"type\": \"text\", \"text\": \"" + TEXT + "\"}, \"properties\": []," +
            "\"headers\": {\"JMSMessageID\": \"ID:1234\", \"JMSDestination\": \"queue:" + DESTINATION_ID + "\"}}";

    private static final MessageModelFactory MESSAGE_MODEL_FACTORY = new MessageModelFactory(
            new HeadersModelFactory(), new PropertyModelFactory(), new BodyModelFactory());

    private MockRestServiceServer mockRestServiceServer;

    private Producer producer;

    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
        producer = new Producer(HOST, restTemplate, PRODUCER_ID, MESSAGE_MODEL_FACTORY);
    }

    @Test
    public void sendMessage() {
        Message message = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody(TEXT));
        message.setId("ID:1234");
        message.setDestination(DESTINATION);

        mockRestServiceServer.expect(once(), requestTo(SEND_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(JSON, true))
                .andRespond(withSuccess());

        producer.sendMessage(message);

        mockRestServiceServer.verify();
    }

    @Test
    public void close() {
        mockRestServiceServer.expect(once(), requestTo(CLOSE_URL))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        producer.close();

        mockRestServiceServer.verify();
    }
}
