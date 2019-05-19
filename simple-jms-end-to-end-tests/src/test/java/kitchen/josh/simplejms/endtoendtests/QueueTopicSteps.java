package kitchen.josh.simplejms.endtoendtests;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.client.*;
import kitchen.josh.simplejms.common.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration
@SpringBootTest(classes = Broker.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueueTopicSteps {

    private static final String[] TEXT = {"a", "b", "c", "d"};
    private static final Serializable[] OBJECTS = {2, "b", 3.4, false};
    private static final String[] PROPERTY_NAMES = {"prop a", "prop b", "property c", "another one d", "e", "this-prop-f", "G", "prop-h"};
    private static final Object[] PROPERTY_VALUES = {false, (byte) 1, (short) 2, 3, 4L, 1.2f, 2.3, "hello"};

    @LocalServerPort
    private int port;

    private Session session;
    private Destination destination;
    private Producer producer;
    private Consumer consumer1;
    private Consumer consumer2;
    private Throwable throwable;

    @Before
    public void setUp() {
        session = new Session("http://localhost:" + port, new RestTemplate());
    }

    @After
    public void tearDown() {
        session = null;
        destination = null;
        producer = null;
        consumer1 = null;
        consumer2 = null;
        throwable = null;
    }

    @Given("a fake destination with a producer and consumer")
    public void a_fake_destination_with_a_producer_and_consumer() {
        destination = new Destination(DestinationType.QUEUE, UUID.randomUUID());
        producer = new Producer("http://localhost:" + port, new RestTemplate(), new ProducerId(destination, UUID.randomUUID()), new MessageModelFactory(new PropertyModelFactory(), new BodyModelFactory()));
        consumer1 = new Consumer("http://localhost:" + port, new RestTemplate(), new ConsumerId(destination, UUID.randomUUID()), new MessageFactory(new PropertiesFactory(), new BodyFactory()));
    }

    @Given("a queue with a producer")
    public void a_queue_with_a_producer() {
        destination = session.createDestination(DestinationType.QUEUE);
        producer = session.createProducer(destination);
    }

    @Given("a queue with a producer and multiple consumers")
    public void a_queue_with_a_producer_and_multiple_consumers() {
        destination = session.createDestination(DestinationType.QUEUE);
        producer = session.createProducer(destination);
        consumer1 = session.createConsumer(destination);
        consumer2 = session.createConsumer(destination);
    }

    @Given("a topic with a producer")
    public void a_topic_with_a_producer() {
        destination = session.createDestination(DestinationType.TOPIC);
        producer = session.createProducer(destination);
    }

    @Given("a topic with a producer and multiple consumers")
    public void a_topic_with_a_producer_and_multiple_consumers() {
        destination = session.createDestination(DestinationType.TOPIC);
        producer = session.createProducer(destination);
        consumer1 = session.createConsumer(destination);
        consumer2 = session.createConsumer(destination);
    }

    @When("a consumer is created")
    public void a_consumer_is_created() {
        consumer1 = session.createConsumer(destination);
    }

    @When("the producer sends messages")
    public void the_producer_sends_messages() {
        producer.sendMessage(session.createTextMessage(TEXT[0]));
        producer.sendMessage(session.createObjectMessage(OBJECTS[1]));
        producer.sendMessage(session.createTextMessage(TEXT[2]));
        producer.sendMessage(session.createObjectMessage(OBJECTS[3]));
    }

    @When("the producer sends messages with properties")
    public void the_producer_sends_messages_with_properties() throws Exception {
        Message message1 = session.createTextMessage(TEXT[0]);
        message1.setObjectProperty(PROPERTY_NAMES[0], PROPERTY_VALUES[0]);
        message1.setObjectProperty(PROPERTY_NAMES[1], PROPERTY_VALUES[1]);

        Message message2 = session.createObjectMessage(OBJECTS[1]);
        message2.setObjectProperty(PROPERTY_NAMES[2], PROPERTY_VALUES[2]);
        message2.setObjectProperty(PROPERTY_NAMES[3], PROPERTY_VALUES[3]);

        Message message3 = session.createTextMessage(TEXT[2]);
        message3.setObjectProperty(PROPERTY_NAMES[4], PROPERTY_VALUES[4]);
        message3.setObjectProperty(PROPERTY_NAMES[5], PROPERTY_VALUES[5]);

        Message message4 = session.createObjectMessage(OBJECTS[3]);
        message4.setObjectProperty(PROPERTY_NAMES[6], PROPERTY_VALUES[6]);
        message4.setObjectProperty(PROPERTY_NAMES[7], PROPERTY_VALUES[7]);

        producer.sendMessage(message1);
        producer.sendMessage(message2);
        producer.sendMessage(message3);
        producer.sendMessage(message4);
    }

    @When("the producer tries to send a message")
    public void the_producer_tries_to_send_a_message() {
        try {
            producer.sendMessage(session.createTextMessage(TEXT[0]));
        } catch (Throwable t) {
            throwable = t;
        }
    }

    @When("the consumer tries to receive a message")
    public void the_consumer_tries_to_receive_a_message() {
        try {
            consumer1.receiveMessage();
        } catch (Throwable t) {
            throwable = t;
        }
    }

    @Then("each message is only received by a single consumer")
    public void each_message_is_only_received_by_a_single_consumer() {
        Optional<Message> message1 = consumer1.receiveMessage();
        Optional<Message> message2 = consumer2.receiveMessage();
        Optional<Message> message3 = consumer2.receiveMessage();
        Optional<Message> message4 = consumer1.receiveMessage();

        assertThat(message1).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(TEXT[0]));
        assertThat(message2).get().isEqualToComparingFieldByFieldRecursively(session.createObjectMessage(OBJECTS[1]));
        assertThat(message3).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(TEXT[2]));
        assertThat(message4).get().isEqualToComparingFieldByFieldRecursively(session.createObjectMessage(OBJECTS[3]));

        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
        assertThat(consumer2.receiveMessage()).isEmpty();
    }

    @Then("the consumer receives messages")
    public void the_consumer_receives_messages() {
        Optional<Message> message1 = consumer1.receiveMessage();
        Optional<Message> message2 = consumer1.receiveMessage();
        Optional<Message> message3 = consumer1.receiveMessage();
        Optional<Message> message4 = consumer1.receiveMessage();

        assertThat(message1).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(TEXT[0]));
        assertThat(message2).get().isEqualToComparingFieldByFieldRecursively(session.createObjectMessage(OBJECTS[1]));
        assertThat(message3).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(TEXT[2]));
        assertThat(message4).get().isEqualToComparingFieldByFieldRecursively(session.createObjectMessage(OBJECTS[3]));

        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();
    }

    @Then("the consumer receives messages with properties")
    public void the_consumer_receives_messages_with_properties() throws Exception {
        Message message1 = session.createTextMessage(TEXT[0]);
        message1.setObjectProperty(PROPERTY_NAMES[0], PROPERTY_VALUES[0]);
        message1.setObjectProperty(PROPERTY_NAMES[1], PROPERTY_VALUES[1]);

        Message message2 = session.createObjectMessage(OBJECTS[1]);
        message2.setObjectProperty(PROPERTY_NAMES[2], PROPERTY_VALUES[2]);
        message2.setObjectProperty(PROPERTY_NAMES[3], PROPERTY_VALUES[3]);

        Message message3 = session.createTextMessage(TEXT[2]);
        message3.setObjectProperty(PROPERTY_NAMES[4], PROPERTY_VALUES[4]);
        message3.setObjectProperty(PROPERTY_NAMES[5], PROPERTY_VALUES[5]);

        Message message4 = session.createObjectMessage(OBJECTS[3]);
        message4.setObjectProperty(PROPERTY_NAMES[6], PROPERTY_VALUES[6]);
        message4.setObjectProperty(PROPERTY_NAMES[7], PROPERTY_VALUES[7]);

        Optional<Message> received1 = consumer1.receiveMessage();
        Optional<Message> received2 = consumer1.receiveMessage();
        Optional<Message> received3 = consumer1.receiveMessage();
        Optional<Message> received4 = consumer1.receiveMessage();

        assertThat(received1).get().isEqualToComparingFieldByFieldRecursively(message1);
        assertThat(received2).get().isEqualToComparingFieldByFieldRecursively(message2);
        assertThat(received3).get().isEqualToComparingFieldByFieldRecursively(message3);
        assertThat(received4).get().isEqualToComparingFieldByFieldRecursively(message4);

        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();
    }

    @Then("each message is received by every consumer")
    public void each_message_is_received_by_every_consumer() {
        Stream.of(consumer1, consumer2).forEach(consumer -> {
            Optional<Message> message1 = consumer.receiveMessage();
            Optional<Message> message2 = consumer.receiveMessage();
            Optional<Message> message3 = consumer.receiveMessage();
            Optional<Message> message4 = consumer.receiveMessage();

            assertThat(message1).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(TEXT[0]));
            assertThat(message2).get().isEqualToComparingFieldByFieldRecursively(session.createObjectMessage(OBJECTS[1]));
            assertThat(message3).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(TEXT[2]));
            assertThat(message4).get().isEqualToComparingFieldByFieldRecursively(session.createObjectMessage(OBJECTS[3]));

            assertThat(consumer.receiveMessage()).isEmpty();
            assertThat(consumer.receiveMessage()).isEmpty();
        });
    }

    @Then("the consumer receives no messages")
    public void the_consumer_receives_no_messages() {
        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();
    }

    @Then("an exception was thrown")
    public void an_exception_was_thrown() {
        assertThat(throwable).isNotNull();
    }
}
