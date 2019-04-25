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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * Integration tests for the Broker's independence of queue and topic functionality.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Broker.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class QueueAndTopicTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Sending messages to a topic does not send messages to queue consumers.
     */
    @Test
    public void queueConsumersDoNotReceiveTopicMessages() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID consumerId = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/topic/" + topicId + "/producer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/topic/" + topicId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/" + queueId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Queue consumers can never receive a message using the topic receive endpoint.
     */
    @Test
    public void queueConsumersCannotReceiveUsingTopicEndpoint() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID queueProducerId = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();
        UUID topicProducerId = restTemplate.postForEntity("/topic/" + queueId + "/consumer", null, IdModel.class).getBody().getId();

        UUID queueConsumerId = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/queue/" + queueId + "/producer/" + queueProducerId + "/send", new MessageModel("hello world"), Void.class);
        restTemplate.postForEntity("/topic/" + topicId + "/producer/" + topicProducerId + "/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + queueId + "/consumer/" + queueConsumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Sending messages to a queue does not send messages to topic consumers.
     */
    @Test
    public void topicConsumersDoNotReceiveQueueMessages() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID consumerId = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();
        UUID producerId = restTemplate.postForEntity("/queue/" + queueId + "/producer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/queue/" + queueId + "/producer/" + producerId + "/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/" + topicId + "/consumer/" + consumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Topic consumers can never receive a message using the queue receive endpoint.
     */
    @Test
    public void topicConsumersCannotReceiveUsingQueueEndpoint() {
        UUID queueId = restTemplate.postForEntity("/queue", null, IdModel.class).getBody().getId();
        UUID topicId = restTemplate.postForEntity("/topic", null, IdModel.class).getBody().getId();
        UUID queueProducerId = restTemplate.postForEntity("/queue/" + queueId + "/consumer", null, IdModel.class).getBody().getId();
        UUID topicProducerId = restTemplate.postForEntity("/topic/" + queueId + "/consumer", null, IdModel.class).getBody().getId();

        UUID topicConsumerId = restTemplate.postForEntity("/topic/" + topicId + "/consumer", null, IdModel.class).getBody().getId();

        restTemplate.postForEntity("/queue/" + queueId + "/producer/" + queueProducerId + "/send", new MessageModel("hello world"), Void.class);
        restTemplate.postForEntity("/topic/" + topicId + "/producer/" + topicProducerId + "/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/" + topicId + "/consumer/" + topicConsumerId + "/receive", null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }
}
