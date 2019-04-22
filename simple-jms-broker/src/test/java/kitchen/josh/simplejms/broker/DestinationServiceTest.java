package kitchen.josh.simplejms.broker;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DestinationServiceTest {

    private DestinationService destinationService;

    @Before
    public void setUp() {
        destinationService = new DestinationService();
    }

    @Test
    public void createDestination_QUEUE_createsQueue() {
        UUID queueId = destinationService.createDestination(Destination.QUEUE);

        assertThat(queueId).isNotNull();
        assertThat(destinationService.getQueues()).containsOnlyKeys(queueId);
        assertThat(destinationService.getQueues().get(queueId)).isInstanceOf(SingleQueueService.class);
        assertThat(destinationService.getTopics()).isEmpty();
    }

    @Test
    public void createDestination_TOPIC_createsTopic() {
        UUID topicId = destinationService.createDestination(Destination.TOPIC);

        assertThat(topicId).isNotNull();
        assertThat(destinationService.getTopics()).containsOnlyKeys(topicId);
        assertThat(destinationService.getTopics().get(topicId)).isInstanceOf(SingleTopicService.class);
        assertThat(destinationService.getQueues()).isEmpty();
    }

    @Test
    public void findDestination_noDestination_returnsEmpty() {
        assertThat(destinationService.findDestination(Destination.TOPIC, UUID.randomUUID())).isEmpty();
        assertThat(destinationService.findDestination(Destination.QUEUE, UUID.randomUUID())).isEmpty();
    }

    @Test
    public void findDestination_queue_returnsForQueue() {
        UUID queueId = destinationService.createDestination(Destination.QUEUE);

        Optional<SingleDestinationService> service = destinationService.findDestination(Destination.QUEUE, queueId);
        assertThat(service).contains(destinationService.getQueues().get(queueId));

        assertThat(destinationService.findDestination(Destination.QUEUE, UUID.randomUUID())).isEmpty();
        assertThat(destinationService.findDestination(Destination.TOPIC, UUID.randomUUID())).isEmpty();
        assertThat(destinationService.findDestination(Destination.TOPIC, queueId)).isEmpty();
    }

    @Test
    public void findDestination_topic_returnsForTopic() {
        UUID topicId = destinationService.createDestination(Destination.TOPIC);

        Optional<SingleDestinationService> service = destinationService.findDestination(Destination.TOPIC, topicId);
        assertThat(service).contains(destinationService.getTopics().get(topicId));

        assertThat(destinationService.findDestination(Destination.TOPIC, UUID.randomUUID())).isEmpty();
        assertThat(destinationService.findDestination(Destination.TOPIC, UUID.randomUUID())).isEmpty();
        assertThat(destinationService.findDestination(Destination.QUEUE, topicId)).isEmpty();
    }
}