package kitchen.josh.simplejms.broker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
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
        assertThat(topicService.getConsumerQueues()).containsOnlyKeys(consumerId);
        Queue<String> consumerQueue = topicService.getConsumerQueues().get(consumerId);
        assertThat(consumerQueue).isEmpty();
    }

    @Test
    public void createConsumer_createsUniqueUUIDs() {
        Set<UUID> consumerIds = IntStream.range(0, 10)
                .mapToObj(i -> topicService.createConsumer())
                .collect(Collectors.toSet());

        assertThat(consumerIds).hasSize(10);
        assertThat(topicService.getConsumerQueues()).containsOnlyKeys(new ArrayList<>(consumerIds).toArray(new UUID[]{}));
        assertThat(topicService.getConsumerQueues().values()).allMatch(Collection::isEmpty);
    }

    @Test
    public void addMessage_noConsumers_doesNothing() {
        topicService.addMessage(MESSAGE_1);

        assertThat(topicService.getConsumerQueues()).isEmpty();
    }

    @Test
    public void addMessage_consumersExist_appendsMessageToQueues() {
        topicService.createConsumer();
        topicService.createConsumer();

        topicService.addMessage(MESSAGE_1);
        topicService.addMessage(MESSAGE_2);

        assertThat(topicService.getConsumerQueues()).hasSize(2);
        topicService.getConsumerQueues().values().forEach(queue -> {
            assertThat(queue.poll()).isEqualTo(MESSAGE_1);
            assertThat(queue.poll()).isEqualTo(MESSAGE_2);
        });
    }

    @Test
    public void readMessage_consumerDoesNotExist_returnsEmpty() {
        Optional<String> message = topicService.readMessage(UUID.randomUUID());
        assertThat(message).isEmpty();
    }

    @Test
    public void readMessage_consumerExists_emptyQueue_returnsEmpty() {
        UUID consumerId = topicService.createConsumer();
        Optional<String> message = topicService.readMessage(consumerId);
        assertThat(message).isEmpty();
    }

    @Test
    public void readMessage_consumerExistsWithMessage_returnsAndRemovesMessage() {
        UUID consumerId = topicService.createConsumer();
        topicService.addMessage(MESSAGE_1);
        topicService.addMessage(MESSAGE_2);

        Optional<String> message = topicService.readMessage(consumerId);

        assertThat(message).contains(MESSAGE_1);
        assertThat(topicService.getConsumerQueues().get(consumerId)).containsOnly(MESSAGE_2);
    }

    @Test
    public void readMessage_multipleConsumers_onlyRemovesThisConsumersMessage() {
        UUID consumerId = topicService.createConsumer();
        UUID otherId = topicService.createConsumer();

        topicService.addMessage(MESSAGE_1);
        topicService.addMessage(MESSAGE_2);

        Optional<String> message = topicService.readMessage(consumerId);
        assertThat(message).contains(MESSAGE_1);
        assertThat(topicService.getConsumerQueues().get(consumerId)).containsOnly(MESSAGE_2);
        assertThat(topicService.getConsumerQueues().get(otherId)).containsOnly(MESSAGE_1, MESSAGE_2);
    }
}