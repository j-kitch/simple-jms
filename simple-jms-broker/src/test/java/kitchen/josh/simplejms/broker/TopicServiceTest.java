package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TopicServiceTest {

    private static final UUID ID = UUID.randomUUID();

    private Message[] messages;

    private TopicService topicService;

    @Before
    public void setUp() {
        messages = createMessages();
        topicService = new TopicService(ID);
    }

    @Test
    public void createConsumer_shouldCreateUUIDAndAddEntryWithEmptyQueue() {
        UUID consumerId = topicService.createConsumer();

        assertThat(consumerId).isNotNull();
        assertThat(topicService.getConsumerQueues()).containsOnlyKeys(consumerId);
        Queue<Message> consumerQueue = topicService.getConsumerQueues().get(consumerId);
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
        topicService.addMessage(producerId, messages[0]);
        topicService.addMessage(producerId, messages[1]);

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
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), messages[0]));

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_noConsumers_doesNothing() {
        UUID producerId = topicService.createProducer();
        topicService.addMessage(producerId, messages[0]);

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void addMessage_producerDoesNotExist_consumersExist_throwsProducerDoesNotExist() {
        topicService.createConsumer();
        topicService.createConsumer();

        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), messages[0]));
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), messages[1]));

        assertThat(topicService.getConsumerQueues()).hasSize(2);
        topicService.getConsumerQueues().values().forEach(queue -> assertThat(queue).isEmpty());
        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_consumersExist_appendsMessageToQueues() {
        UUID producerId = topicService.createProducer();
        topicService.createConsumer();
        topicService.createConsumer();

        topicService.addMessage(producerId, messages[0]);
        topicService.addMessage(producerId, messages[1]);

        assertThat(topicService.getConsumerQueues()).hasSize(2);
        topicService.getConsumerQueues().values().forEach(queue -> {
            assertThat(queue).containsExactly(messages[0], messages[1]);
        });
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void addMessage_setsDestinationToThis() {
        UUID producerId = topicService.createProducer();
        topicService.createConsumer();
        topicService.createConsumer();

        topicService.addMessage(producerId, messages[0]);
        topicService.addMessage(producerId, messages[1]);

        topicService.getConsumerQueues().values().forEach(queue ->
                assertThat(queue)
                        .extracting(Message::getDestination)
                        .containsExactly(new Destination(DestinationType.TOPIC, ID), new Destination(DestinationType.TOPIC, ID)));
    }

    @Test
    public void addMessage_setsMessageId() {
        UUID producerId = topicService.createProducer();
        topicService.createConsumer();
        topicService.createConsumer();

        topicService.addMessage(producerId, messages[0]);
        topicService.addMessage(producerId, messages[1]);

        List<Queue<Message>> queues = new ArrayList<>(topicService.getConsumerQueues().values());
        List<Message> messages1 = new ArrayList<>(queues.get(0));
        List<Message> messages2 = new ArrayList<>(queues.get(1));

        for (int i = 0; i < 2; i++) {
            // IDs should be the same for the same message in each consumer's queue.
            assertThat(messages1.get(i).getId()).isEqualTo(messages2.get(i).getId());
        }

        // IDs should be unique
        assertThat(messages1.get(0).getId()).isNotEqualTo(messages1.get(1).getId());

        // IDs should be ID:<UUID> format.
        assertThat(messages1)
                .extracting(Message::getId)
                .allSatisfy(id -> {
                    String[] parts = id.split(":");
                    assertThat(parts[0]).isEqualTo("ID");
                    assertThat(UUID.fromString(parts[1])).isNotNull();
                });
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
        Optional<Message> message = topicService.readMessage(consumerId);
        assertThat(message).isEmpty();
    }

    @Test
    public void readMessage_consumerExistsWithMessage_returnsAndRemovesMessage() {
        UUID producerId = topicService.createProducer();
        UUID consumerId = topicService.createConsumer();
        topicService.addMessage(producerId, messages[0]);
        topicService.addMessage(producerId, messages[1]);

        Optional<Message> message = topicService.readMessage(consumerId);

        assertThat(message).contains(messages[0]);
        assertThat(topicService.getConsumerQueues().get(consumerId)).containsExactly(messages[1]);
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void readMessage_multipleConsumers_onlyRemovesThisConsumersMessage() {
        UUID producerId = topicService.createProducer();
        UUID consumerId = topicService.createConsumer();
        UUID otherId = topicService.createConsumer();

        topicService.addMessage(producerId, messages[0]);
        topicService.addMessage(producerId, messages[1]);

        Optional<Message> message = topicService.readMessage(consumerId);

        assertThat(message).contains(messages[0]);
        assertThat(topicService.getConsumerQueues().get(consumerId)).containsExactly(messages[1]);
        assertThat(topicService.getConsumerQueues().get(otherId)).containsExactly(messages[0], messages[1]);
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    private static Message[] createMessages() {
        TextMessage message1 = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody("hello world"));
        ObjectMessage message2 = new ObjectMessage(new HeadersImpl(), new PropertiesImpl(), new ObjectBody(2));
        TextMessage message3 = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody("abcd"));
        ObjectMessage message4 = new ObjectMessage(new HeadersImpl(), new PropertiesImpl(), new ObjectBody(12.3));

        message1.setIntProperty("prop1", 2);
        message1.setFloatProperty("prop2", 2.3f);

        message2.setDoubleProperty("prop1", 2.3);
        message2.setShortProperty("prop", (short) 12);

        message3.setBooleanProperty("a", false);
        message3.setStringProperty("b", "hello");

        message4.setObject(12.3);

        return new Message[]{message1, message2, message3, message4};
    }
}