package kitchen.josh.simplejms.endtoendtests;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.Session;
import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.TextMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = RANDOM_PORT)
public class AcknowledgeTest {

    private static final String[] TEXTS = {"a", "b", "c", "d", "e", "f"};

    @LocalServerPort
    private int port;

    private Session session;

    @Before
    public void setUp() {
        session = new Session("http://localhost:" + port, new RestTemplate());
    }

    /**
     * Unacknowledged messages are re-sent on Consumer.recover(), before new message are sent.
     */
    @Test
    public void unacknowledgedMessagesAreReSentOnRecover() {
        Destination destination = session.createDestination(DestinationType.TOPIC);
        Producer producer = session.createProducer(destination);
        Consumer consumer = session.createConsumer(destination);

        producer.sendMessage(session.createTextMessage(TEXTS[0]));
        producer.sendMessage(session.createTextMessage(TEXTS[1]));
        producer.sendMessage(session.createTextMessage(TEXTS[2]));
        producer.sendMessage(session.createTextMessage(TEXTS[3]));
        producer.sendMessage(session.createTextMessage(TEXTS[4]));
        producer.sendMessage(session.createTextMessage(TEXTS[5]));

        consumer.receiveMessage();
        consumer.receiveMessage();
        consumer.receiveMessage();
        consumer.receiveMessage();

        consumer.recover();

        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[0]);
        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[1]);
        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[2]);
        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[3]);
        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[4]);
        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[5]);
    }

    /**
     * Acknowledged messages are not re-sent on Consumer.recover().
     */
    @Test
    public void acknowledgedMessagesAreNotReSentOnRecover() {
        Destination destination = session.createDestination(DestinationType.TOPIC);
        Producer producer = session.createProducer(destination);
        Consumer consumer = session.createConsumer(destination);

        producer.sendMessage(session.createTextMessage(TEXTS[0]));
        producer.sendMessage(session.createTextMessage(TEXTS[1]));
        producer.sendMessage(session.createTextMessage(TEXTS[2]));
        producer.sendMessage(session.createTextMessage(TEXTS[3]));
        producer.sendMessage(session.createTextMessage(TEXTS[4]));
        producer.sendMessage(session.createTextMessage(TEXTS[5]));

        consumer.receiveMessage();
        Message acknowledged = consumer.receiveMessage().get();
        consumer.receiveMessage();
        consumer.receiveMessage();

        consumer.acknowledge(acknowledged);
        consumer.recover();

        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[2]);
        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[3]);
        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[4]);
        assertThat(((TextMessage) consumer.receiveMessage().get()).getText()).isEqualTo(TEXTS[5]);
    }
}
