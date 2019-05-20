package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.ObjectMessage;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.ObjectBody;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class QueueServiceTest {

    private static final UUID ID = UUID.randomUUID();

    private Message[] messages;

    private QueueService queueService;

    @Before
    public void setUp() {
        messages = createMessages();
        queueService = new QueueService(ID);
    }

    @Test
    public void createConsumer_shouldCreateNewUUIDAndAddToConsumers() {
        UUID consumerId = queueService.createConsumer();

        assertThat(consumerId).isNotNull();
        assertThat(queueService.getConsumers()).containsOnly(consumerId);
    }

    @Test
    public void createProducer_shouldCreateNewUUIDAndAddToProducers() {
        UUID producerId = queueService.createProducer();

        assertThat(producerId).isNotNull();
        assertThat(queueService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void removeConsumer_removesConsumer() {
        UUID consumerId = queueService.createConsumer();

        queueService.removeConsumer(consumerId);

        assertThat(queueService.getConsumers()).isEmpty();
    }

    @Test
    public void removeConsumer_consumerDoesNotExist_throwsConsumerDoesNotExist() {
        assertThatExceptionOfType(ConsumerDoesNotExistException.class)
                .isThrownBy(() -> queueService.removeConsumer(UUID.randomUUID()));

        assertThat(queueService.getMessages()).isEmpty();
        assertThat(queueService.getConsumers()).isEmpty();
        assertThat(queueService.getProducers()).isEmpty();
    }

    @Test
    public void removeProducer_removesProducer() {
        UUID producerId = queueService.createProducer();

        queueService.removeProducer(producerId);

        assertThat(queueService.getProducers()).isEmpty();
    }

    @Test
    public void removeProducer_producerDoesNotExist_throwsProducerDoesNotExist() {
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> queueService.removeProducer(UUID.randomUUID()));

        assertThat(queueService.getMessages()).isEmpty();
        assertThat(queueService.getConsumers()).isEmpty();
        assertThat(queueService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_producerDoesNotExist_throwsProducerDoesNotExist() {
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> queueService.addMessage(UUID.randomUUID(), messages[0]));
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> queueService.addMessage(UUID.randomUUID(), messages[1]));

        assertThat(queueService.getMessages()).isEmpty();
        assertThat(queueService.getConsumers()).isEmpty();
        assertThat(queueService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_appendsMessageToMessages() {
        UUID producerId = queueService.createProducer();
        queueService.addMessage(producerId, messages[0]);
        queueService.addMessage(producerId, messages[1]);

        assertThat(queueService.getMessages()).containsExactly(messages[0], messages[1]);
    }

    @Test
    public void addMessage_setsDestinationToThis() {
        UUID producerId = queueService.createProducer();
        queueService.addMessage(producerId, messages[0]);
        queueService.addMessage(producerId, messages[1]);

        assertThat(queueService.getMessages())
                .extracting(Message::getDestination)
                .containsExactly(new Destination(DestinationType.QUEUE, ID), new Destination(DestinationType.QUEUE, ID));
    }

    @Test
    public void addMessage_setsMessageId() {
        UUID producerId = queueService.createProducer();
        queueService.createConsumer();
        queueService.createConsumer();

        queueService.addMessage(producerId, messages[0]);
        queueService.addMessage(producerId, messages[1]);

        List<Message> messages = new ArrayList<>(queueService.getMessages());

        // IDs should be unique
        assertThat(messages.get(0).getId()).isNotEqualTo(messages.get(1).getId());

        // IDs should be ID:<UUID> format.
        assertThat(messages)
                .extracting(Message::getId)
                .allSatisfy(id -> {
                    String[] parts = id.split(":");
                    assertThat(parts[0]).isEqualTo("ID");
                    assertThat(UUID.fromString(parts[1])).isNotNull();
                });
    }

    @Test
    public void readMessage_consumerDoesNotExist_throwsConsumerDoesNotExist() {
        UUID producerId = queueService.createProducer();
        queueService.addMessage(producerId, messages[0]);

        assertThatExceptionOfType(ConsumerDoesNotExistException.class)
                .isThrownBy(() -> queueService.readMessage(UUID.randomUUID()));

        assertThat(queueService.getConsumers()).isEmpty();
        assertThat(queueService.getMessages()).containsExactly(messages[0]);
        assertThat(queueService.getProducers()).containsOnly(producerId);
    }

    @Test
    public void readMessage_consumerExistsNoMessages_returnsEmpty() {
        UUID consumerId = queueService.createConsumer();

        Optional<Message> read = queueService.readMessage(consumerId);

        assertThat(read).isEmpty();
        assertThat(queueService.getConsumers()).containsExactly(consumerId);
        assertThat(queueService.getProducers()).isEmpty();
        assertThat(queueService.getMessages()).isEmpty();
    }

    @Test
    public void readMessage_consumerExistsWithMessages_popsFirst() {
        UUID producerId = queueService.createProducer();
        UUID consumerId = queueService.createConsumer();
        queueService.addMessage(producerId, messages[0]);
        queueService.addMessage(producerId, messages[1]);

        Optional<Message> read = queueService.readMessage(consumerId);

        assertThat(read).contains(messages[0]);
        assertThat(queueService.getConsumers()).containsExactly(consumerId);
        assertThat(queueService.getProducers()).containsOnly(producerId);
        assertThat(queueService.getMessages()).containsExactly(messages[1]);
    }

    @Test
    public void readMessage_multipleConsumers_popFromSameQueue() {
        UUID producerId = queueService.createProducer();
        queueService.addMessage(producerId, messages[0]);
        queueService.addMessage(producerId, messages[1]);
        queueService.addMessage(producerId, messages[2]);
        queueService.addMessage(producerId, messages[3]);
        UUID consumerId1 = queueService.createConsumer();
        UUID consumerId2 = queueService.createConsumer();

        Optional<Message> read1 = queueService.readMessage(consumerId1);
        Optional<Message> read2 = queueService.readMessage(consumerId2);
        Optional<Message> read3 = queueService.readMessage(consumerId1);
        Optional<Message> read4 = queueService.readMessage(consumerId2);
        Optional<Message> read5 = queueService.readMessage(consumerId2);
        Optional<Message> read6 = queueService.readMessage(consumerId1);

        assertThat(read1).contains(messages[0]);
        assertThat(read2).contains(messages[1]);
        assertThat(read3).contains(messages[2]);
        assertThat(read4).contains(messages[3]);
        assertThat(read5).isEmpty();
        assertThat(read6).isEmpty();
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