package kitchen.josh.simplejms.integrationtests;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.broker.ConsumerId;
import kitchen.josh.simplejms.broker.MessageModel;
import kitchen.josh.simplejms.client.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * Broker's Topic REST API functionality.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class BrokerTopicApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Calling the create topic consumer endpoint returns unique consumer IDs.
     */
    @Test
    public void createTopicConsumer_returnsUniqueIds() {
        Set<UUID> consumerIds = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            ResponseEntity<ConsumerId> response = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            ConsumerId consumerId = response.getBody();
            assertThat(consumerIds).doesNotContain(consumerId.getId());
            consumerIds.add(consumerId.getId());
        }
    }

    /**
     * Unknown consumer IDs receive empty messages.
     */
    @Test
    public void receiveMessage_unknownConsumerId_returnsOkAndEmptyMessage() {
        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/receive/" + UUID.randomUUID(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * When no messages exist to receive, a consumer receives an empty message.
     */
    @Test
    public void receiveMessage_noMessages_returnsOkAndEmptyMessage() {
        ConsumerId consumerId = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class).getBody();

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * When a message is sent before a consumer is created, the consumer does not receive the message.
     */
    @Test
    public void receiveMessage_messageSentBeforeConsumer_returnsOkAndEmptyMessage() {
        restTemplate.postForEntity("/topic/send", new MessageModel("hello world"), Void.class);
        ConsumerId consumerId = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class).getBody();

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * When a message is sent after a consumer is created, the consumer receives the message.
     */
    @Test
    public void receiveMessage_messageSentAfterConsumer_returnsOkAndMessage() {
        ConsumerId consumerId = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class).getBody();
        restTemplate.postForEntity("/topic/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));
    }

    /**
     * A message is only received once by a consumer.
     */
    @Test
    public void receiveMessage_receivesMessageOnce() {
        ConsumerId consumerId = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class).getBody();
        restTemplate.postForEntity("/topic/send", new MessageModel("hello world"), Void.class);

        // Receives message on first call.
        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));

        // Receives empty on the second call.
        ResponseEntity<MessageModel> response2 = restTemplate.postForEntity("/topic/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Every consumer receives the same message.
     */
    @Test
    public void receiveMessage_multipleConsumers_allReceiveMessages() {
        ConsumerId consumerId1 = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class).getBody();
        ConsumerId consumerId2 = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class).getBody();

        restTemplate.postForEntity("/topic/send", new MessageModel("hello world"), Void.class);

        // Consumer 1 receives message.
        ResponseEntity<MessageModel> response1 = restTemplate.postForEntity("/topic/receive/" + consumerId1.getId(), null, MessageModel.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response1.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));

        // Consumer2 receives message.
        ResponseEntity<MessageModel> response2 = restTemplate.postForEntity("/topic/receive/" + consumerId2.getId(), null, MessageModel.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));
    }

    /**
     * Sending a message returns OK and an empty body.
     */
    @Test
    public void sendMessage_returnsOkAndEmptyBody() {
        ResponseEntity<Void> response = restTemplate.postForEntity("/topic/send", new MessageModel("hello world"), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }
}
