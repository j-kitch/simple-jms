package kitchen.josh.simplejms.broker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TopicServiceTest {

    private static final String MESSAGE_1 = "hello world";
    private static final String MESSAGE_2 = "HELLO WORLD";

    private TopicService topicService;

    @Before
    public void setUp() {
        topicService = new TopicService();
    }

    @Test
    public void createConsumer_shouldCreateUUIDAndAddEntryWithEmptyQueue() {
        UUID consumerId = topicService.createConsumer();

        assertThat(consumerId).isNotNull();
        assertThat(topicService.getConsumerMap()).containsOnlyKeys(consumerId);
        Queue<String> consumerQueue = topicService.getConsumerMap().get(consumerId);
        assertThat(consumerQueue).isEmpty();
    }

    @Test
    public void createConsumer_createsUniqueUUIDs() {
        Set<UUID> consumerIds = IntStream.range(0, 10)
                .mapToObj(i -> topicService.createConsumer())
                .collect(Collectors.toSet());

        assertThat(consumerIds).hasSize(10);
        assertThat(topicService.getConsumerMap()).containsOnlyKeys(consumerIds);
        assertThat(topicService.getConsumerMap().values()).allMatch(Collection::isEmpty);
    }

    @Test
    public void addMessage_noConsumers_doesNothing() {
        topicService.addMessage(MESSAGE_1);

        assertThat(topicService.getConsumerMap()).isEmpty();
    }

    @Test
    public void addMessage_consumersExist_appendsMessageToQueues() {
        topicService.createConsumer();
        topicService.createConsumer();

        topicService.addMessage(MESSAGE_1);
        topicService.addMessage(MESSAGE_2);

        assertThat(topicService.getConsumerMap()).hasSize(2);
        topicService.getConsumerMap().values().forEach(queue -> {
            assertThat(queue.poll()).isEqualTo(MESSAGE_1);
            assertThat(queue.poll()).isEqualTo(MESSAGE_2);
        });
    }
}