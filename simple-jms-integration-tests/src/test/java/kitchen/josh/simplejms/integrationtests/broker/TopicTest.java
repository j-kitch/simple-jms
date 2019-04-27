package kitchen.josh.simplejms.integrationtests.broker;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.broker.IdModel;
import kitchen.josh.simplejms.broker.MessageModel;
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
 * Integration tests for the Broker's topic functionality.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TopicTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Calling the create topic consumer endpoint returns unique consumer IDs.
     */
    @Test
    public void createTopicConsumer_returnsUniqueIds() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();

        Set<UUID> consumerIds = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            ResponseEntity<IdModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            IdModel consumerId = response.getBody();
            assertThat(consumerIds).doesNotContain(consumerId.getId());
            consumerIds.add(consumerId.getId());
        }
    }

    /**
     * Unknown consumer IDs receive empty messages.
     */
    @Test
    public void receiveMessage_unknownConsumerId_returnsOkAndEmptyMessage() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + UUID.randomUUID() + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * When no messages exist to receive, a consumer receives an empty message.
     */
    @Test
    public void receiveMessage_noMessages_returnsOkAndEmptyMessage() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID consumerId = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * When a message is sent before a consumer is created, the consumer does not receive the message.
     */
    @Test
    public void receiveMessage_messageSentBeforeConsumer_returnsOkAndEmptyMessage() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/topic/" + topicId + "/producer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/topic/" + topicId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);
        UUID consumerId = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * When a message is sent after a consumer is created, the consumer receives the message.
     */
    @Test
    public void receiveMessage_messageSentAfterConsumer_returnsOkAndMessage() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/topic/" + topicId + "/producer", null, IdModel.class).getBody().getId();

        UUID consumerId = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();
        restTemplate.postForEntity("/topic/" + topicId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * A message is only received once by a consumer.
     */
    @Test
    public void receiveMessage_receivesMessageOnce() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/topic/" + topicId + "/producer", null, IdModel.class).getBody().getId();
        UUID consumerId = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/topic/" + topicId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);

        // Receives message on first call.
        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));

        // Receives empty on the second call.
        ResponseEntity<MessageModel> response2 = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Every consumer receives the same message.
     */
    @Test
    public void receiveMessage_multipleConsumers_allReceiveMessages() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/topic/" + topicId + "/producer", null, IdModel.class).getBody().getId();
        UUID consumer1Id = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();
        UUID consumer2Id = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/topic/" + topicId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);

        // One consumer receives the message.
        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumer1Id + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));

        // The other consumer receives the message.
        ResponseEntity<MessageModel> response2 = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumer2Id + "/receive", null, MessageModel.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));
    }

    /**
     * Sending a message returns OK and an empty body.
     */
    @Test
    public void sendMessage_returnsOkAndEmptyBody() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/topic/" + topicId + "/producer", null, IdModel.class).getBody().getId();

        ResponseEntity<Void> response = restTemplate.postForEntity("/topic/" + topicId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void deleteConsumer_consumerExists_cannotReceiveMessages() {
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID consumerId = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/topic/" + topicId + "/producer/", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/topic/" + topicId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);
        restTemplate.delete("/topic/" + topicId + "/consumer/" + consumerId, null, Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }
}
