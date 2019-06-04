package kitchen.josh.simplejms.endtoendtests;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.ConsumerId;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.ProducerId;
import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.MessageModelFactory;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.BodyFactory;
import kitchen.josh.simplejms.common.message.body.BodyModelFactory;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.HeadersFactory;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.headers.HeadersModelFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import kitchen.josh.simplejms.common.message.properties.PropertyModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = RANDOM_PORT)
public class DestinationTest {

    @LocalServerPort
    private int port;

    private String host;

    @Before
    public void setUp() {
        host = "http://localhost:" + port;
    }

    /**
     * A message cannot be sent to a destination that does not exist.
     */
    @Test
    public void messageCannotBeSentToNonExistentDestination() {
        Destination destination = new Destination(DestinationType.QUEUE, UUID.randomUUID());
        Producer producer = new Producer(host, new RestTemplate(), new ProducerId(destination, UUID.randomUUID()), new MessageModelFactory(
                new HeadersModelFactory(), new PropertyModelFactory(), new BodyModelFactory()));

        TextMessage message = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody());
        message.setText("hello world");

        assertThatExceptionOfType(RestClientException.class).isThrownBy(() -> producer.sendMessage(message));
    }

    /**
     * A message cannot be received from a destination that does not exist.
     */
    @Test
    public void messageCannotBeReceivedFromNonExistentDestination() {
        Destination destination = new Destination(DestinationType.QUEUE, UUID.randomUUID());
        Consumer consumer = new Consumer(host, new RestTemplate(), new ConsumerId(destination, UUID.randomUUID()), new MessageFactory(
                new HeadersFactory(), new PropertiesFactory(), new BodyFactory()));

        TextMessage message = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody());
        message.setText("hello world");

        assertThatExceptionOfType(RestClientException.class).isThrownBy(consumer::receiveMessage);
    }
}
