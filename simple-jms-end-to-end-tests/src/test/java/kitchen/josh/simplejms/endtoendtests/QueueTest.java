package kitchen.josh.simplejms.endtoendtests;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.Session;
import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.body.ObjectBody;
import kitchen.josh.simplejms.common.message.body.TextBody;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = RANDOM_PORT)
public class QueueTest {

    private static final String[] TEXTS = {"a", "b"};
    private static final Serializable[] OBJECTS = {2, UUID.randomUUID()};

    @LocalServerPort
    private int port;

    private Session session;

    @Before
    public void setUp() {
        session = new Session("http://localhost:" + port, new RestTemplate());
    }

    /**
     * Each message is only received by a single consumer.
     */
    @Test
    public void onlyOneConsumerReceivesEachMessage() {
        Destination destination = session.createDestination(DestinationType.QUEUE);
        Producer producer = session.createProducer(destination);
        Consumer consumer1 = session.createConsumer(destination);
        Consumer consumer2 = session.createConsumer(destination);

        producer.sendMessage(session.createTextMessage(TEXTS[0]));
        producer.sendMessage(session.createObjectMessage(OBJECTS[0]));
        producer.sendMessage(session.createTextMessage(TEXTS[1]));
        producer.sendMessage(session.createObjectMessage(OBJECTS[1]));

        assertThat(consumer1.receiveMessage().get().getBody()).isEqualToComparingFieldByField(new TextBody(TEXTS[0]));
        assertThat(consumer2.receiveMessage().get().getBody()).isEqualToComparingFieldByField(new ObjectBody(OBJECTS[0]));
        assertThat(consumer2.receiveMessage().get().getBody()).isEqualToComparingFieldByField(new TextBody(TEXTS[1]));
        assertThat(consumer1.receiveMessage().get().getBody()).isEqualToComparingFieldByField(new ObjectBody(OBJECTS[1]));
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
    }

    /**
     * Messages are saved to the queue even when consumers don't exist.
     */
    @Test
    public void messagesAreSavedIrrespectiveOfConsumers() {
        Destination destination = session.createDestination(DestinationType.QUEUE);
        Producer producer = session.createProducer(destination);

        producer.sendMessage(session.createTextMessage(TEXTS[0]));
        producer.sendMessage(session.createObjectMessage(OBJECTS[0]));
        producer.sendMessage(session.createTextMessage(TEXTS[1]));
        producer.sendMessage(session.createObjectMessage(OBJECTS[1]));

        Consumer consumer1 = session.createConsumer(destination);
        Consumer consumer2 = session.createConsumer(destination);

        assertThat(consumer1.receiveMessage().get().getBody()).isEqualToComparingFieldByField(new TextBody(TEXTS[0]));
        assertThat(consumer2.receiveMessage().get().getBody()).isEqualToComparingFieldByField(new ObjectBody(OBJECTS[0]));
        assertThat(consumer2.receiveMessage().get().getBody()).isEqualToComparingFieldByField(new TextBody(TEXTS[1]));
        assertThat(consumer1.receiveMessage().get().getBody()).isEqualToComparingFieldByField(new ObjectBody(OBJECTS[1]));
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
    }
}
