package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
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
        UUID queueId = destinationService.createDestination(DestinationType.QUEUE);

        assertThat(queueId).isNotNull();
        assertThat(destinationService.getQueues()).containsOnlyKeys(queueId);
        assertThat(destinationService.getQueues().get(queueId)).isInstanceOf(QueueService.class);
        assertThat(destinationService.getTopics()).isEmpty();
    }

    @Test
    public void createDestination_TOPIC_createsTopic() {
        UUID topicId = destinationService.createDestination(DestinationType.TOPIC);

        assertThat(topicId).isNotNull();
        assertThat(destinationService.getTopics()).containsOnlyKeys(topicId);
        assertThat(destinationService.getTopics().get(topicId)).isInstanceOf(TopicService.class);
        assertThat(destinationService.getQueues()).isEmpty();
    }

    @Test
    public void findDestination_noDestination_returnsEmpty() {
        assertThat(destinationService.findDestination(new Destination(DestinationType.TOPIC, UUID.randomUUID()))).isEmpty();
        assertThat(destinationService.findDestination(new Destination(DestinationType.QUEUE, UUID.randomUUID()))).isEmpty();
    }

    @Test
    public void findDestination_queue_returnsForQueue() {
        UUID queueId = destinationService.createDestination(DestinationType.QUEUE);

        Optional<SingleDestinationService> service = destinationService.findDestination(new Destination(DestinationType.QUEUE, queueId));
        assertThat(service).contains(destinationService.getQueues().get(queueId));

        assertThat(destinationService.findDestination(new Destination(DestinationType.QUEUE, UUID.randomUUID()))).isEmpty();
        assertThat(destinationService.findDestination(new Destination(DestinationType.TOPIC, UUID.randomUUID()))).isEmpty();
        assertThat(destinationService.findDestination(new Destination(DestinationType.TOPIC, queueId))).isEmpty();
    }

    @Test
    public void findDestination_topic_returnsForTopic() {
        UUID topicId = destinationService.createDestination(DestinationType.TOPIC);

        Optional<SingleDestinationService> service = destinationService.findDestination(new Destination(DestinationType.TOPIC, topicId));
        assertThat(service).contains(destinationService.getTopics().get(topicId));

        assertThat(destinationService.findDestination(new Destination(DestinationType.TOPIC, UUID.randomUUID()))).isEmpty();
        assertThat(destinationService.findDestination(new Destination(DestinationType.TOPIC, UUID.randomUUID()))).isEmpty();
        assertThat(destinationService.findDestination(new Destination(DestinationType.QUEUE, topicId))).isEmpty();
    }
}