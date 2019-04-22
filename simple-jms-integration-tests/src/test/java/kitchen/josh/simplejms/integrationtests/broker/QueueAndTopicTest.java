package kitchen.josh.simplejms.integrationtests.broker;

import kitchen.josh.simplejms.broker.Broker;
import kitchen.josh.simplejms.broker.ConsumerId;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

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
        ConsumerId consumerId = restTemplate.postForEntity("/queue/consumer", null, ConsumerId.class).getBody();
        restTemplate.postForEntity("/topic/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Queue consumers can never receive a message using the topic receive endpoint.
     */
    @Test
    public void queueConsumersCannotReceiveUsingTopicEndpoint() {
        ConsumerId consumerId = restTemplate.postForEntity("/queue/consumer", null, ConsumerId.class).getBody();
        restTemplate.postForEntity("/queue/send", new MessageModel("hello world"), Void.class);
        restTemplate.postForEntity("/topic/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Sending messages to a queue does not send messages to topic consumers.
     */
    @Test
    public void topicConsumersDoNotReceiveQueueMessages() {
        ConsumerId consumerId = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class).getBody();
        restTemplate.postForEntity("/queue/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/topic/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }

    /**
     * Topic consumers can never receive a message using the queue receive endpoint.
     */
    @Test
    public void topicConsumersCannotReceiveUsingQueueEndpoint() {
        ConsumerId consumerId = restTemplate.postForEntity("/topic/consumer", null, ConsumerId.class).getBody();
        restTemplate.postForEntity("/queue/send", new MessageModel("hello world"), Void.class);
        restTemplate.postForEntity("/topic/send", new MessageModel("hello world"), Void.class);

        ResponseEntity<MessageModel> response = restTemplate.postForEntity("/queue/receive/" + consumerId.getId(), null, MessageModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(new MessageModel(null));
    }
}
