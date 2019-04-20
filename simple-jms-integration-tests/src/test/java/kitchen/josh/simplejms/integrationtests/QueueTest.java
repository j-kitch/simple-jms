package kitchen.josh.simplejms.integrationtests;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.Message;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class QueueTest {

    private static final String[] MESSAGES = {"a", "b", "c", "d"};

    @LocalServerPort
    private int port;

    private String host;

    private Session session;

    @Before
    public void setUp() {
        host = "http://localhost:" + port;
        session = new Session(host, new RestTemplate());
    }

    /**
     * Given a queue consumer and no queue producers,
     * Then the consumer receives no messages.
     */
    @Test
    public void consumerNoProducer_receivesNoMessages() {
        Consumer consumer = session.createConsumer(Destination.QUEUE);

        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and queue producer,
     * When the producer sends no messages,
     * Then the consumer receives no messages.
     */
    @Test
    public void consumerProducer_noMessagesSent_noMessagesReceived() {
        Consumer consumer = session.createConsumer(Destination.QUEUE);
        session.createProducer(Destination.QUEUE);

        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and topic producer,
     * When the producer sends messages,
     * The consumer receives no messages.
     */
    @Test
    public void consumerAndTopicProducer_messagesSent_noMessagesReceived() {
        Consumer consumer = session.createConsumer(Destination.QUEUE);
        Producer producer = session.createProducer(Destination.TOPIC);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);

        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and topic/queue producers,
     * When the producers send messages,
     * Then the consumer only receives queue messages.
     */
    @Test
    public void consumerAndAllDestinationsProducers_messagesSent_onlyQueueMessagesReceived() {
        Consumer consumer = session.createConsumer(Destination.QUEUE);
        Producer topicProducer = session.createProducer(Destination.TOPIC);
        Producer queueProducer = session.createProducer(Destination.QUEUE);

        topicProducer.sendMessage(MESSAGES[0]);
        topicProducer.sendMessage(MESSAGES[1]);
        queueProducer.sendMessage(MESSAGES[2]);
        queueProducer.sendMessage(MESSAGES[3]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[3]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and queue producer,
     * When the producer sends messages,
     * Then the consumer receives the messages in order.
     */
    @Test
    public void singleConsumerSingleProducer_messagesSent_messagesReceived() {
        Consumer consumer = session.createConsumer(Destination.QUEUE);
        Producer producer = session.createProducer(Destination.QUEUE);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);
        producer.sendMessage(MESSAGES[2]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[1]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and multiple queue producers,
     * When the producers send messages,
     * Then the consumer receives the messages in order.
     */
    @Test
    public void singleConsumerMultipleProducers_messagesSent_messagesReceived() {
        Consumer consumer = session.createConsumer(Destination.QUEUE);
        Producer producer1 = session.createProducer(Destination.QUEUE);
        Producer producer2 = session.createProducer(Destination.QUEUE);

        producer1.sendMessage(MESSAGES[0]);
        producer2.sendMessage(MESSAGES[1]);
        producer2.sendMessage(MESSAGES[2]);
        producer1.sendMessage(MESSAGES[3]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[1]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[3]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given multiple queue consumers,
     * When a producer sends messages,
     * Then each message is only received by one consumer.
     */
    @Test
    public void multipleConsumers_eachMessageReceivedOnlyOnce() {
        Consumer consumer1 = session.createConsumer(Destination.QUEUE);
        Consumer consumer2 = session.createConsumer(Destination.QUEUE);
        Producer producer = session.createProducer(Destination.QUEUE);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);
        producer.sendMessage(MESSAGES[2]);
        producer.sendMessage(MESSAGES[3]);

        assertThat(consumer1.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[0]));
        assertThat(consumer2.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[1]));
        assertThat(consumer1.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[2]));
        assertThat(consumer2.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(Destination.QUEUE, MESSAGES[3]));
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
    }
}
