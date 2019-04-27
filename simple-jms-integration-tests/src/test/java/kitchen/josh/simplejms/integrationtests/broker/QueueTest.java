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
 * Integration tests for the Broker's queue functionality.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class QueueTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Calling the create queue consumer endpoint returns unique consumer IDs.
     */
    @Test
    public void createQueueConsumer_returnsUniqueIds() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        Set<UUID> consumerIds = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            ResponseEntity<IdModel> response = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class);
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
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + UUID.randomUUID() + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * When no messages exist to receive, a consumer receives an empty message.
     */
    @Test
    public void receiveMessage_noMessages_returnsOkAndEmptyMessage() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID consumerId = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * When a message is sent before a consumer is created, the consumer receives the message.
     */
    @Test
    public void receiveMessage_messageSentBeforeConsumer_returnsOkAndMessage() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/queue/" + queueId + "/producer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/queue/" + queueId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);
        UUID consumerId = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));
    }

    /**
     * When a message is sent after a consumer is created, the consumer receives the message.
     */
    @Test
    public void receiveMessage_messageSentAfterConsumer_returnsOkAndMessage() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/queue/" + queueId + "/producer", null, IdModel.class).getBody().getId();

        UUID consumerId = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();
        restTemplate.postForEntity("/queue/" + queueId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * A message is only received once by a consumer.
     */
    @Test
    public void receiveMessage_receivesMessageOnce() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/queue/" + queueId + "/producer", null, IdModel.class).getBody().getId();
        UUID consumerId = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/queue/" + queueId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);

        // Receives message on first call.
        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));

        // Receives empty on the second call.
        ResponseEntity<MessageModel> response2 = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Only one consumer receives the same message.
     */
    @Test
    public void receiveMessage_multipleConsumers_onlyOneReceivesMessage() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/queue/" + queueId + "/producer", null, IdModel.class).getBody().getId();
        UUID consumer1Id = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();
        UUID consumer2Id = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/queue/" + queueId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);

        // One consumer receives the message.
        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + consumer1Id + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel("hello world"));

        // The other consumer doesn't receive the message.
        ResponseEntity<MessageModel> response2 = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + consumer2Id + "/receive", null, MessageModel.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Sending a message returns OK and an empty body.
     */
    @Test
    public void sendMessage_returnsOkAndEmptyBody() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/queue/" + queueId + "/producer", null, IdModel.class).getBody().getId();

        ResponseEntity<Void> response = restTemplate.postForEntity("/queue/" + queueId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }
}
