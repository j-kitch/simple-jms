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

    private static final Message[] MESSAGES = createMessages();

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

    private static TextBody createTextBody(String text) {
        TextBody textBody = new TextBody();
        textBody.setText(text);
        return textBody;
    }

    @Test
    public void removeConsumer_removesConsumerAndQueue() {
        UUID producerId = topicService.createProducer();
        UUID consumerId = topicService.createConsumer();
        topicService.addMessage(producerId, MESSAGES[0]);
        topicService.addMessage(producerId, MESSAGES[1]);

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
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), MESSAGES[0]));

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_noConsumers_doesNothing() {
        UUID producerId = topicService.createProducer();
        topicService.addMessage(producerId, MESSAGES[0]);

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void addMessage_producerDoesNotExist_consumersExist_throwsProducerDoesNotExist() {
        topicService.createConsumer();
        topicService.createConsumer();

        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), MESSAGES[0]));
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> topicService.addMessage(UUID.randomUUID(), MESSAGES[1]));

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

        topicService.addMessage(producerId, MESSAGES[0]);
        topicService.addMessage(producerId, MESSAGES[1]);

        assertThat(topicService.getConsumerQueues()).hasSize(2);
        topicService.getConsumerQueues().values().forEach(queue -> {
            assertThat(queue.poll()).isEqualTo(MESSAGES[0]);
            assertThat(queue.poll()).isEqualTo(MESSAGES[1]);
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
        Optional<Message> message = topicService.readMessage(consumerId);
        assertThat(message).isEmpty();
    }

    @Test
    public void readMessage_consumerExistsWithMessage_returnsAndRemovesMessage() {
        UUID producerId = topicService.createProducer();
        UUID consumerId = topicService.createConsumer();
        topicService.addMessage(producerId, MESSAGES[0]);
        topicService.addMessage(producerId, MESSAGES[1]);

        Optional<Message> message = topicService.readMessage(consumerId);

        assertThat(message).contains(MESSAGES[0]);
        assertThat(topicService.getConsumerQueues().get(consumerId)).containsExactly(MESSAGES[1]);
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void readMessage_multipleConsumers_onlyRemovesThisConsumersMessage() {
        UUID producerId = topicService.createProducer();
        UUID consumerId = topicService.createConsumer();
        UUID otherId = topicService.createConsumer();

        topicService.addMessage(producerId, MESSAGES[0]);
        topicService.addMessage(producerId, MESSAGES[1]);

        Optional<Message> message = topicService.readMessage(consumerId);

        assertThat(message).contains(MESSAGES[0]);
        assertThat(topicService.getConsumerQueues().get(consumerId)).containsExactly(MESSAGES[1]);
        assertThat(topicService.getConsumerQueues().get(otherId)).containsExactly(MESSAGES[0], MESSAGES[1]);
        assertThat(topicService.getProducers()).containsOnly(producerId);
    }

    private static Message[] createMessages() {
        TextMessage message1 = new TextMessage(new PropertiesImpl(), new TextBody());
        ObjectMessage message2 = new ObjectMessage(new PropertiesImpl(), new ObjectBody());
        TextMessage message3 = new TextMessage(new PropertiesImpl(), new TextBody());
        ObjectMessage message4 = new ObjectMessage(new PropertiesImpl(), new ObjectBody());

        message1.setIntProperty("prop1", 2);
        message1.setFloatProperty("prop2", 2.3f);
        message1.setText("hello world");

        message2.setDoubleProperty("prop1", 2.3);
        message2.setShortProperty("prop", (short) 12);
        message2.setObject(2);

        message3.setBooleanProperty("a", false);
        message3.setStringProperty("b", "hello");
        message3.setText("abcd");

        message4.setObject(12.3);

        return new Message[]{message1, message2, message3, message4};
    }
}