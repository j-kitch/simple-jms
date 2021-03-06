package kitchen.josh.simplejms.endtoendtests;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.Session;
import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = RANDOM_PORT)
public class MultipleDestinationTest {

    private static class DestinationSetup {
        Destination destination;
        Producer producer;
        Consumer consumer;
        String[] messages;
    }

    @LocalServerPort
    private int port;

    private String host;
    private Session session;
    private DestinationSetup destinationSetup1;
    private DestinationSetup destinationSetup2;

    @Before
    public void setUp() {
        host = "http://localhost:" + port;
        session = new Session(host, new RestTemplate());
    }

    @After
    public void tearDown() {
        session = null;
        destinationSetup1 = null;
        destinationSetup2 = null;
    }

    /**
     * Messages sent to unique destinations are only received by that destination's consumers.
     */
    @Test
    public void eachDestinationIsUnique() {
        destinationSetup1 = new DestinationSetup();
        destinationSetup1.destination = session.createDestination(DestinationType.QUEUE);
        destinationSetup1.producer = session.createProducer(destinationSetup1.destination);
        destinationSetup1.consumer = session.createConsumer(destinationSetup1.destination);
        destinationSetup1.messages = new String[]{"a", "b", "c", "d"};

        destinationSetup2 = new DestinationSetup();
        destinationSetup2.destination = session.createDestination(DestinationType.TOPIC);
        destinationSetup2.producer = session.createProducer(destinationSetup2.destination);
        destinationSetup2.consumer = session.createConsumer(destinationSetup2.destination);
        destinationSetup2.messages = new String[]{"e", "f", "g", "h"};

        Stream.of(destinationSetup1, destinationSetup2).forEach(destinationSetup -> {
            destinationSetup.producer.sendMessage(session.createTextMessage(destinationSetup.messages[0]));
            destinationSetup.producer.sendMessage(session.createTextMessage(destinationSetup.messages[1]));
            destinationSetup.producer.sendMessage(session.createTextMessage(destinationSetup.messages[2]));
            destinationSetup.producer.sendMessage(session.createTextMessage(destinationSetup.messages[3]));
        });

        Stream.of(destinationSetup1, destinationSetup2).forEach(destinationSetup -> {
            Optional<Message> message1 = destinationSetup.consumer.receiveMessage();
            Optional<Message> message2 = destinationSetup.consumer.receiveMessage();
            Optional<Message> message3 = destinationSetup.consumer.receiveMessage();
            Optional<Message> message4 = destinationSetup.consumer.receiveMessage();

            assertThat(message1.get().getProperties()).isEqualToComparingFieldByField(new PropertiesImpl());
            assertThat(message2.get().getProperties()).isEqualToComparingFieldByField(new PropertiesImpl());
            assertThat(message3.get().getProperties()).isEqualToComparingFieldByField(new PropertiesImpl());
            assertThat(message4.get().getProperties()).isEqualToComparingFieldByField(new PropertiesImpl());

            assertThat(message1.get().getBody()).isEqualToComparingFieldByField(new TextBody(destinationSetup.messages[0]));
            assertThat(message2.get().getBody()).isEqualToComparingFieldByField(new TextBody(destinationSetup.messages[1]));
            assertThat(message3.get().getBody()).isEqualToComparingFieldByField(new TextBody(destinationSetup.messages[2]));
            assertThat(message4.get().getBody()).isEqualToComparingFieldByField(new TextBody(destinationSetup.messages[3]));

            assertThat(destinationSetup.consumer.receiveMessage()).isEmpty();
            assertThat(destinationSetup.consumer.receiveMessage()).isEmpty();
        });
    }
}
