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
}