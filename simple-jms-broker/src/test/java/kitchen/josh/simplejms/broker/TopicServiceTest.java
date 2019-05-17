package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.OldMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TopicServiceTest {

    private static final Destination DESTINATION = new Destination(DestinationType.TOPIC, UUID.randomUUID());
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
        Queue<OldMessage> consumerQueue = topicService.getConsumerQueues().get(consumerId);
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
    public void createProducer_shouldCreateUUIDAndAddEntry() {
        UUID producerId = topicService.createProducer();

        assertThat(producerId).isNotNull();
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void createProducer_createsUniqueUUIDs() {
        Set<UUID> producerIds = IntStream.range(0, 10)
                .mapToObj(i -> topicService.createProducer())
                .collect(Collectors.toSet());

        assertThat(producerIds).hasSize(10);
        assertThat(topicService.getProducers()).containsOnly(new ArrayList<>(producerIds).toArray(new UUID[]{}));
    }

    @Test
    public void removeConsumer_removesConsumerAndQueue() {
        UUID producerId = topicService.createProducer();
        UUID consumerId = topicService.createConsumer();
        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_1));
        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_2));

        topicService.removeConsumer(consumerId);

        assertThat(topicService.getConsumerQueues()).isEmpty();
    }

    @Test
    public void removeConsumer_consumerDoesNotExist_throwsConsumerDoesNotExistException() {
        assertThatExceptionOfType(ConsumerDoesNotExistException.class)
                .isThrownBy(() -> topicService.removeConsumer(UUID.randomUUID()));

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void removeProducer_removesProducer() {
        UUID producerId = topicService.createProducer();

        topicService.removeProducer(producerId);

        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void removeProducer_producerDoesNotExist_throwsProducerDoesNotExistException() {
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> topicService.removeProducer(UUID.randomUUID()));

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_producerDoesNotExist_throwsProducerDoesNotExist() {
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), new OldMessage(DESTINATION, MESSAGE_1)));

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_noConsumers_doesNothing() {
        UUID producerId = topicService.createProducer();
        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_1));

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void addMessage_producerDoesNotExist_consumersExist_throwsProducerDoesNotExist() {
        topicService.createConsumer();
        topicService.createConsumer();

        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), new OldMessage(DESTINATION, MESSAGE_1)));
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), new OldMessage(DESTINATION, MESSAGE_2)));

        assertThat(topicService.getConsumerQueues()).hasSize(2);
        topicService.getConsumerQueues().values().forEach(queue -> {
            assertThat(queue).isEmpty();
        });
        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_consumersExist_appendsMessageToQueues() {
        UUID producerId = topicService.createProducer();
        topicService.createConsumer();
        topicService.createConsumer();

        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_1));
        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_2));

        assertThat(topicService.getConsumerQueues()).hasSize(2);
        topicService.getConsumerQueues().values().forEach(queue -> {
            assertThat(queue.poll()).isEqualToComparingFieldByFieldRecursively(new OldMessage(DESTINATION, MESSAGE_1));
            assertThat(queue.poll()).isEqualToComparingFieldByFieldRecursively(new OldMessage(DESTINATION, MESSAGE_2));
        });
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void readMessage_consumerDoesNotExist_throwsConsumerDoesNotExist() {
        assertThatExceptionOfType(ConsumerDoesNotExistException.class)
                .isThrownBy(() -> topicService.readMessage(UUID.randomUUID()));

        assertThat(topicService.getProducers()).isEmpty();
        assertThat(topicService.getConsumerQueues()).isEmpty();
    }

    @Test
    public void readMessage_consumerExists_emptyQueue_returnsEmpty() {
        UUID consumerId = topicService.createConsumer();
        Optional<OldMessage> message = topicService.readMessage(consumerId);
        assertThat(message).isEmpty();
    }

    @Test
    public void readMessage_consumerExistsWithMessage_returnsAndRemovesMessage() {
        UUID producerId = topicService.createProducer();
        UUID consumerId = topicService.createConsumer();
        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_1));
        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_2));

        Optional<OldMessage> message = topicService.readMessage(consumerId);

        assertThat(message).get().isEqualToComparingFieldByFieldRecursively(new OldMessage(DESTINATION, MESSAGE_1));
        assertThat(topicService.getConsumerQueues().get(consumerId)).usingRecursiveFieldByFieldElementComparator().containsOnly(new OldMessage(DESTINATION, MESSAGE_2));
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void readMessage_multipleConsumers_onlyRemovesThisConsumersMessage() {
        UUID producerId = topicService.createProducer();
        UUID consumerId = topicService.createConsumer();
        UUID otherId = topicService.createConsumer();

        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_1));
        topicService.addMessage(producerId, new OldMessage(DESTINATION, MESSAGE_2));

        Optional<OldMessage> message = topicService.readMessage(consumerId);
        assertThat(message).get().isEqualToComparingFieldByFieldRecursively(new OldMessage(DESTINATION, MESSAGE_1));
        assertThat(topicService.getConsumerQueues().get(consumerId)).usingRecursiveFieldByFieldElementComparator()
                .containsOnly(new OldMessage(DESTINATION, MESSAGE_2));
        assertThat(topicService.getConsumerQueues().get(otherId)).usingRecursiveFieldByFieldElementComparator().containsOnly(
                new OldMessage(DESTINATION, MESSAGE_1),
                new OldMessage(DESTINATION, MESSAGE_2));
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }
}