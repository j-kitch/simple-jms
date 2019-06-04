package kitchen.josh.simplejms.endtoendtests;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.client.Consumer;
import kitchen.josh.simplejms.client.Producer;
import kitchen.josh.simplejms.client.Session;
import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = RANDOM_PORT)
public class PropertiesTest {

    private static final String[] PROPERTY_NAMES = {"prop a", "prop b", "property c", "another one d", "e", "this-prop-f", "G", "prop-h"};
    private static final Object[] PROPERTY_VALUES = {false, (byte) 1, (short) 2, 3, 4L, 1.2f, 2.3, "hello"};

    @LocalServerPort
    private int port;

    private Session session;

    @Before
    public void setUp() {
        session = new Session("http://localhost:" + port, new RestTemplate());
    }

    @Test
    public void sentPropertiesAreReceived() throws Exception {
        Destination destination = session.createDestination(DestinationType.TOPIC);
        Producer producer = session.createProducer(destination);
        Consumer consumer1 = session.createConsumer(destination);
        Consumer consumer2 = session.createConsumer(destination);

        // Send messages
        {
            Message message1 = session.createTextMessage("a");
            Message message2 = session.createObjectMessage(2);
            Message message3 = session.createTextMessage("");
            Message message4 = session.createObjectMessage(UUID.randomUUID());

            message1.setObjectProperty(PROPERTY_NAMES[0], PROPERTY_VALUES[0]);
            message1.setObjectProperty(PROPERTY_NAMES[1], PROPERTY_VALUES[1]);

            message2.setObjectProperty(PROPERTY_NAMES[2], PROPERTY_VALUES[2]);
            message2.setObjectProperty(PROPERTY_NAMES[3], PROPERTY_VALUES[3]);

            message3.setObjectProperty(PROPERTY_NAMES[4], PROPERTY_VALUES[4]);
            message3.setObjectProperty(PROPERTY_NAMES[5], PROPERTY_VALUES[5]);

            message4.setObjectProperty(PROPERTY_NAMES[6], PROPERTY_VALUES[6]);
            message4.setObjectProperty(PROPERTY_NAMES[7], PROPERTY_VALUES[7]);

            producer.sendMessage(message1);
            producer.sendMessage(message2);
            producer.sendMessage(message3);
            producer.sendMessage(message4);
        }

        Stream.of(consumer1, consumer2).forEach(consumer -> {
            Message message1 = consumer.receiveMessage().get();
            assertThat(message1.getObjectProperty(PROPERTY_NAMES[0])).isEqualTo(PROPERTY_VALUES[0]);
            assertThat(message1.getObjectProperty(PROPERTY_NAMES[1])).isEqualTo(PROPERTY_VALUES[1]);

            Message message2 = consumer.receiveMessage().get();
            assertThat(message2.getObjectProperty(PROPERTY_NAMES[2])).isEqualTo(PROPERTY_VALUES[2]);
            assertThat(message2.getObjectProperty(PROPERTY_NAMES[3])).isEqualTo(PROPERTY_VALUES[3]);

            Message message3 = consumer.receiveMessage().get();
            assertThat(message3.getObjectProperty(PROPERTY_NAMES[4])).isEqualTo(PROPERTY_VALUES[4]);
            assertThat(message3.getObjectProperty(PROPERTY_NAMES[5])).isEqualTo(PROPERTY_VALUES[5]);

            Message message4 = consumer.receiveMessage().get();
            assertThat(message4.getObjectProperty(PROPERTY_NAMES[6])).isEqualTo(PROPERTY_VALUES[6]);
            assertThat(message4.getObjectProperty(PROPERTY_NAMES[7])).isEqualTo(PROPERTY_VALUES[7]);
        });
    }
}
