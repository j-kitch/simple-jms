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
 * Integration tests for the interaction between the Broker and Client API for topic functionality.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TopicTest {

    private static final Destination QUEUE = new Destination(DestinationType.QUEUE, null);
    private static final Destination TOPIC = new Destination(DestinationType.TOPIC, null);

    private static final String[] MESSAGES = {
            "hello world",
            "HELLO world",
            "hello WORLD",
            "HELLO WORLD"
    };

    @LocalServerPort
    private int port;

    private Session session;

    @Before
    public void setUp() {
        String host = "http://localhost:" + port;
        session = new Session(host, new RestTemplate());
    }

    /**
     * Given a single consumer and a single producer exist,
     * When the producer sends multiple messages,
     * Then the consumer receives these messages in order,
     * And then the consumer receives no messages.
     */
    @Test
    public void singleConsumerSingleProducer_sentMessages_receivedMessagesInOrder() {
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(topic);
        Producer producer = session.createProducer(topic);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);
        producer.sendMessage(MESSAGES[2]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[1]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given multiple consumers and a single producer,
     * When the producer sends multiple messages,
     * Then the consumers all receive the messages in order,
     * And then the consumers receive no messages.
     */
    @Test
    public void multipleConsumersSingleProducer_sentMessages_allConsumersReceiveMessagesInOrder() {
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Producer producer = session.createProducer(topic);
        Consumer consumer1 = session.createConsumer(topic);
        Consumer consumer2 = session.createConsumer(topic);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);
        producer.sendMessage(MESSAGES[2]);

        assertThat(consumer1.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[0]));
        assertThat(consumer1.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[1]));
        assertThat(consumer1.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[2]));
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();

        assertThat(consumer2.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[0]));
        assertThat(consumer2.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[1]));
        assertThat(consumer2.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[2]));
        assertThat(consumer2.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
    }

    /**
     * Given a producer exists,
     * And the producer has sent messages,
     * When a consumer is created,
     * And the producer sends more messages,
     * Then the consumer only receives the messages after it's construction.
     */
    @Test
    public void consumerOnlyReceivesMessagesAfterConstruction() {
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Producer producer = session.createProducer(topic);

        producer.sendMessage(MESSAGES[0]);
        producer.sendMessage(MESSAGES[1]);

        Consumer consumer = session.createConsumer(topic);

        producer.sendMessage(MESSAGES[2]);
        producer.sendMessage(MESSAGES[3]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[3]));
        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given only a single consumer exists,
     * And no messages are sent,
     * Then the consumer receives no messages.
     */
    @Test
    public void onlyConsumerReceivesNoMessages() {
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(topic);

        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given only a single consumer and a single producer,
     * When the producer sends no messages,
     * Then the consumer receives no messages.
     */
    @Test
    public void singleConsumerSingleProducer_noMessagesSent_noMessagesReceived() {
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(topic);
        session.createProducer(topic);

        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a topic consumer and a different topic producer,
     * When the producer sends a message,
     * Then the consumer receives no messages.
     */
    @Test
    public void singleConsumerAndOtherTopicProducer_messageSent_noMessageReceived() {
        Destination topic1 = session.createDestination(DestinationType.TOPIC);
        Destination topic2 = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(topic1);
        Producer producer = session.createProducer(topic2);

        producer.sendMessage(MESSAGES[0]);

        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a topic consumer and a queue producer,
     * When the queue producer sends a message,
     * Then the topic consumer receives no messages.
     */
    @Test
    public void consumerAndQueueProducer_queueMessageSent_noTopicMessageReceived() {
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(topic);
        Producer producer = session.createProducer(queue);

        producer.sendMessage(MESSAGES[0]);

        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a topic consumer and different topic producers,
     * When producers send messages,
     * Then only this topic's messages are received.
     */
    @Test
    public void consumerAndMultipleDifferentTopicProducers_sendMessages_onlyThisTopicMessagesReceived() {
        Destination topic1 = session.createDestination(DestinationType.TOPIC);
        Destination topic2 = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(topic1);
        Producer producer1 = session.createProducer(topic1);
        Producer producer2 = session.createProducer(topic2);

        producer1.sendMessage(MESSAGES[0]);
        producer2.sendMessage(MESSAGES[1]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic1, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    /**
     * Given a topic consumer, queue producer and topic producer,
     * When each producer sends messages,
     * Then the consumer only receives topic messages.
     */
    @Test
    public void consumerAndAllDestinationProducers_allDestinationMessagesSent_onlyTopicMessagesReceived() {
        Destination topic = session.createDestination(DestinationType.TOPIC);
        Destination queue = session.createDestination(DestinationType.QUEUE);
        Consumer consumer = session.createConsumer(topic);
        Producer topicProducer = session.createProducer(topic);
        Producer queueProducer = session.createProducer(queue);

        queueProducer.sendMessage(MESSAGES[0]);
        queueProducer.sendMessage(MESSAGES[1]);
        topicProducer.sendMessage(MESSAGES[2]);
        topicProducer.sendMessage(MESSAGES[3]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[2]));
        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(topic, MESSAGES[3]));
        assertThat(consumer.receiveMessage()).isEmpty();
        assertThat(consumer.receiveMessage()).isEmpty();
    }

    @Test
    public void consumerClosed_doesNotReceiveMessages() {
        Destination queue = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(queue);
        Producer producer = session.createProducer(queue);

        producer.sendMessage(MESSAGES[0]);

        consumer.close();

        assertThat(consumer.receiveMessage()).isEmpty();
    }

    @Test
    public void producerClosed_doesNotSendMessages() {
        Destination queue = session.createDestination(DestinationType.TOPIC);
        Consumer consumer = session.createConsumer(queue);
        Producer producer = session.createProducer(queue);

        producer.sendMessage(MESSAGES[0]);
        producer.close();
        producer.sendMessage(MESSAGES[1]);

        assertThat(consumer.receiveMessage()).usingFieldByFieldValueComparator().contains(new Message(queue, MESSAGES[0]));
        assertThat(consumer.receiveMessage()).isEmpty();
    }
}
