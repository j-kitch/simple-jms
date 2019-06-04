package kitchen.josh.simplejms.endtoendtests;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.Session;
import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = RANDOM_PORT)
public class HeadersTest {

    @LocalServerPort
    private int port;

    private Session session;

    @Before
    public void setUp() {
        session = new Session("http://localhost:" + port, new RestTemplate());
    }

    /**
     * The destination header is set to the consumer's destination.
     */
    @Test
    public void destinationIsSetToDestination() {
        Destination destination1 = session.createDestination(DestinationType.QUEUE);
        Consumer consumer1 = session.createConsumer(destination1);
        Producer producer1 = session.createProducer(destination1);

        producer1.sendMessage(session.createTextMessage("hello world"));
        producer1.sendMessage(session.createObjectMessage(2));

        assertThat(consumer1.receiveMessage().get().getDestination()).isEqualTo(destination1);
        assertThat(consumer1.receiveMessage().get().getDestination()).isEqualTo(destination1);

        Destination destination2 = session.createDestination(DestinationType.QUEUE);
        Consumer consumer2 = session.createConsumer(destination2);
        Producer producer2 = session.createProducer(destination2);

        producer2.sendMessage(session.createObjectMessage(UUID.randomUUID()));
        producer2.sendMessage(session.createTextMessage(""));

        assertThat(consumer2.receiveMessage().get().getDestination()).isEqualTo(destination2);
        assertThat(consumer2.receiveMessage().get().getDestination()).isEqualTo(destination2);
    }

    /**
     * Each message is returned with a unique message ID header starting with "ID:".
     */
    @Test
    public void messageIdsAreUnique() {
        Set<String> messageIds = new HashSet<>();

        Destination destination1 = session.createDestination(DestinationType.QUEUE);
        Consumer consumer1 = session.createConsumer(destination1);
        Producer producer1 = session.createProducer(destination1);

        producer1.sendMessage(session.createTextMessage("hello world"));
        producer1.sendMessage(session.createObjectMessage(2));

        Destination destination2 = session.createDestination(DestinationType.QUEUE);
        Consumer consumer2 = session.createConsumer(destination2);
        Producer producer2 = session.createProducer(destination2);

        producer2.sendMessage(session.createObjectMessage(UUID.randomUUID()));
        producer2.sendMessage(session.createTextMessage(""));

        messageIds.add(consumer1.receiveMessage().get().getId());
        messageIds.add(consumer1.receiveMessage().get().getId());
        messageIds.add(consumer2.receiveMessage().get().getId());
        messageIds.add(consumer2.receiveMessage().get().getId());

        // All messages are unique and start with "ID:".
        assertThat(messageIds).hasSize(4);
        assertThat(messageIds).allMatch(id -> id.startsWith("ID:"));
    }
}
