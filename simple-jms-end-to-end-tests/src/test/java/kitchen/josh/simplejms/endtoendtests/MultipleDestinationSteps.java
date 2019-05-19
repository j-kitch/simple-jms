package kitchen.josh.simplejms.endtoendtests;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.Session;
import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.Message;
import kitchen.josh.simplejms.common.TextBody;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MultipleDestinationSteps {

    @LocalServerPort
    private int port;

    private Session session;

    private static class DestinationSetup {
        Destination destination;
        Producer producer;
        Consumer consumer;
        String[] messages;
    }

    private DestinationSetup destinationSetup1;
    private DestinationSetup destinationSetup2;

    @Before
    public void setUp() {
        session = new Session("http://localhost:" + port, new RestTemplate());
    }

    @After
    public void tearDown() {
        session = null;
        destinationSetup1 = null;
        destinationSetup2 = null;
    }

    @Given("multiple destinations, each with producers and consumers")
    public void multiple_destinations_each_with_producers_and_consumers() {
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
    }

    @When("each destination's producers send messages")
    public void each_destination_s_producers_send_messages() {
        Stream.of(destinationSetup1, destinationSetup2).forEach(destinationSetup -> {
            destinationSetup.producer.sendMessage(session.createTextMessage(destinationSetup.messages[0]));
            destinationSetup.producer.sendMessage(session.createTextMessage(destinationSetup.messages[1]));
            destinationSetup.producer.sendMessage(session.createTextMessage(destinationSetup.messages[2]));
            destinationSetup.producer.sendMessage(session.createTextMessage(destinationSetup.messages[3]));
        });
    }

    @Then("each destination's consumers only receive their destinations messages")
    public void each_destination_s_consumers_only_receive_their_destinations_messages() {
        Stream.of(destinationSetup1, destinationSetup2).forEach(destinationSetup -> {
            Optional<Message> message1 = destinationSetup.consumer.receiveMessage();
            Optional<Message> message2 = destinationSetup.consumer.receiveMessage();
            Optional<Message> message3 = destinationSetup.consumer.receiveMessage();
            Optional<Message> message4 = destinationSetup.consumer.receiveMessage();

            assertThat(message1).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(destinationSetup.messages[0]));
            assertThat(message2).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(destinationSetup.messages[1]));
            assertThat(message3).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(destinationSetup.messages[2]));
            assertThat(message4).get().isEqualToComparingFieldByFieldRecursively(session.createTextMessage(destinationSetup.messages[3]));

            assertThat(destinationSetup.consumer.receiveMessage()).isEmpty();
            assertThat(destinationSetup.consumer.receiveMessage()).isEmpty();
        });
    }

    private static TextBody createTextBody(String text) {
        TextBody textBody = new TextBody();
        textBody.setText(text);
        return textBody;
    }
}
