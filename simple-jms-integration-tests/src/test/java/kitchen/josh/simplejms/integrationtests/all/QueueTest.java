package kitchen.josh.simplejms.integrationtests.all;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
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

/**
 * Integration tests for the interaction between the Broker and Client API for queue functionality.
 */
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
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(queue);

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
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(queue);
        session.createProducer(queue);

        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and a different queue producer,
     * When the producer sends a message,
     * Then the consumer receives no messages.
     */
    @Test
    public void consumerAndOtherQueueProducer_messageSent_noMessagesReceived() {
        Destination queue1 = session.createDestination(DestinationType.QUEUE);
        Destination queue2 = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(queue1);
        Producer producer = session.createProducer(queue2);

        producer.sendMessage("hello world");

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
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(queue);
        Producer producer = session.createProducer(topic);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);

        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer, and multiple different queue producers,
     * When the producers send messages,
     * The consumer only receives messages from it's queue.
     */
    @Test
    public void consumerAndMultipleQueueProducers_messagesSent_onlyThisQueueMessagesReceived() {
        Destination queue1 = session.createDestination(DestinationType.QUEUE);
        Destination queue2 = session.createDestination(DestinationType.QUEUE);
        Producer producer1 = session.createProducer(queue1);
        Producer producer2 = session.createProducer(queue2);
        Consumer consumer = session.createConsumer(queue1);

        producer1.sendMessage(MESSAGES[0]);
        producer2.sendMessage(MESSAGES[1]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue1, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and topic/queue producers,
     * When the producers send messages,
     * Then the consumer only receives queue messages.
     */
    @Test
    public void consumerAndAllDestinationTypeProducers_messagesSent_onlyQueueMessagesReceived() {
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(queue);
        Producer topicProducer = session.createProducer(topic);
        Producer queueProducer = session.createProducer(queue);

        topicProducer.sendMessage(MESSAGES[0]);
        topicProducer.sendMessage(MESSAGES[1]);
        queueProducer.sendMessage(MESSAGES[2]);
        queueProducer.sendMessage(MESSAGES[3]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[3]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and queue producer,
     * When the producer sends messages,
     * Then the consumer receives the messages in order.
     */
    @Test
    public void singleConsumerSingleProducer_messagesSent_messagesReceived() {
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(queue);
        Producer producer = session.createProducer(queue);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);
        producer.sendMessage(MESSAGES[2]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[1]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a queue consumer and multiple queue producers,
     * When the producers send messages,
     * Then the consumer receives the messages in order.
     */
    @Test
    public void singleConsumerMultipleProducers_messagesSent_messagesReceived() {
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(queue);
        Producer producer1 = session.createProducer(queue);
        Producer producer2 = session.createProducer(queue);

        producer1.sendMessage(MESSAGES[0]);
        producer2.sendMessage(MESSAGES[1]);
        producer2.sendMessage(MESSAGES[2]);
        producer1.sendMessage(MESSAGES[3]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[1]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[3]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given multiple queue consumers,
     * When a producer sends messages,
     * Then each message is only received by one consumer.
     */
    @Test
    public void multipleConsumers_eachMessageReceivedOnlyOnce() {
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer1 = session.createConsumer(queue);
        Consumer consumer2 = session.createConsumer(queue);
        Producer producer = session.createProducer(queue);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);
        producer.sendMessage(MESSAGES[2]);
        producer.sendMessage(MESSAGES[3]);

        assertThat(consumer1.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[0]));
        assertThat(consumer2.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[1]));
        assertThat(consumer1.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[2]));
        assertThat(consumer2.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[3]));
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
    }

    @Test
    public void consumerClosed_doesNotReceiveMessages() {
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(queue);
        Producer producer = session.createProducer(queue);

        producer.sendMessage(MESSAGES[0]);

        consumer.close();

        assertThat(consumer.receiveMessage()).isEmpty();
    }

    @Test
    public void producerClosed_doesNotSendMessages() {
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(queue);
        Producer producer = session.createProducer(queue);

        producer.sendMessage(MESSAGES[0]);
        producer.close();
        producer.sendMessage(MESSAGES[1]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }
}
