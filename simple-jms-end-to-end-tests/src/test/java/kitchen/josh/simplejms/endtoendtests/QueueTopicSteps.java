package kitchen.josh.simplejms.endtoendtests;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.client.*;
import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.Message;
import kitchen.josh.simplejms.common.Properties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration
@SpringBootTest(classes = Broker.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueueTopicSteps {

    private static final String[] MESSAGES = {
            "a", "b", "c", "d"
    };

    private static final Properties[] PROPERTIES = createProperties();

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
        producer = new Producer("http://localhost:" + port, new RestTemplate(), new ProducerId(destination, UUID.randomUUID()));
        consumer1 = new Consumer("http://localhost:" + port, new RestTemplate(), new ConsumerId(destination, UUID.randomUUID()));
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
        producer.sendMessage(new Message(destination, MESSAGES[0]));
        producer.sendMessage(new Message(destination, MESSAGES[1]));
        producer.sendMessage(new Message(destination, MESSAGES[2]));
        producer.sendMessage(new Message(destination, MESSAGES[3]));
    }

    @When("the producer sends messages with properties")
    public void the_producer_sends_messages_with_properties() {
        producer.sendMessage(new Message(destination, PROPERTIES[0], MESSAGES[0]));
        producer.sendMessage(new Message(destination, PROPERTIES[1], MESSAGES[1]));
        producer.sendMessage(new Message(destination, PROPERTIES[2], MESSAGES[2]));
        producer.sendMessage(new Message(destination, PROPERTIES[3], MESSAGES[3]));
    }

    @When("the producer tries to send a message")
    public void the_producer_tries_to_send_a_message() {
        try {
            producer.sendMessage(new Message(destination, MESSAGES[0]));
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

        assertThat(message1).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[0]));
        assertThat(message2).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[1]));
        assertThat(message3).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[2]));
        assertThat(message4).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[3]));

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

        assertThat(message1).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[0]));
        assertThat(message2).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[1]));
        assertThat(message3).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[2]));
        assertThat(message4).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[3]));

        assertThat(consumer1.receiveMessage()).isEmpty();
        assertThat(consumer1.receiveMessage()).isEmpty();
    }

    @Then("the consumer receives messages with properties")
    public void the_consumer_receives_messages_with_properties() {
        Optional<Message> message1 = consumer1.receiveMessage();
        Optional<Message> message2 = consumer1.receiveMessage();
        Optional<Message> message3 = consumer1.receiveMessage();
        Optional<Message> message4 = consumer1.receiveMessage();

        assertThat(message1).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, PROPERTIES[0], MESSAGES[0]));
        assertThat(message2).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, PROPERTIES[1], MESSAGES[1]));
        assertThat(message3).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, PROPERTIES[2], MESSAGES[2]));
        assertThat(message4).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, PROPERTIES[3], MESSAGES[3]));

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

            assertThat(message1).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[0]));
            assertThat(message2).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[1]));
            assertThat(message3).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[2]));
            assertThat(message4).get().isEqualToComparingFieldByFieldRecursively(new Message(destination, MESSAGES[3]));

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

    private static Properties[] createProperties() {
        Properties[] properties = {new Properties(), new Properties(), new Properties(), new Properties()};
        properties[0].setBooleanProperty("property 1", false);
        properties[0].setByteProperty("property 2", (byte) 2);
        properties[1].setShortProperty("property 3", (short) 3);
        properties[1].setIntProperty("property 4", 4);
        properties[2].setLongProperty("property 5", 5);
        properties[2].setFloatProperty("property 6", 1.2f);
        properties[3].setDoubleProperty("property 7", 2.3);
        properties[3].setStringProperty("property 8", "hello world");
        return properties;
    }
}
