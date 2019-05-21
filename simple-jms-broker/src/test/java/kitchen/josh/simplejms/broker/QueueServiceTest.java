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
    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final UUID CONSUMER_ID_1 = UUID.randomUUID();
    private static final UUID CONSUMER_ID_2 = UUID.randomUUID();
    private static final UUID PRODUCER_ID = UUID.randomUUID();
    private static final UUID PRODUCER_ID_1 = UUID.randomUUID();
    private static final UUID PRODUCER_ID_2 = UUID.randomUUID();

    private Message[] messages;

    private QueueService queueService;

    @Before
    public void setUp() {
        messages = createMessages();
        queueService = new QueueService(ID);
    }

    @Test
    public void addConsumer_addsConsumerToConsumers() {
        queueService.addConsumer(ID);

        assertThat(queueService.getConsumers()).containsExactly(ID);
        assertThat(queueService.getProducers()).isEmpty();
        assertThat(queueService.getMessages()).isEmpty();
    }

    @Test
    public void addConsumer_consumerAlreadyExists_throwsIllegalState() {
        queueService.addConsumer(ID);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> queueService.addConsumer(ID));
    }

    @Test
    public void addProducer_addsProducerToProducers() {
        queueService.addProducer(ID);

        assertThat(queueService.getConsumers()).isEmpty();
        assertThat(queueService.getProducers()).containsExactly(ID);
        assertThat(queueService.getMessages()).isEmpty();
    }

    @Test
    public void addProducer_producerAlreadyExists_throwsIllegalState() {
        queueService.addProducer(ID);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> queueService.addProducer(ID));
    }

    @Test
    public void removeConsumer_removesConsumer() {
        queueService.addConsumer(CONSUMER_ID);

        queueService.removeConsumer(CONSUMER_ID);

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
        queueService.addProducer(PRODUCER_ID);

        queueService.removeProducer(PRODUCER_ID);

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
        queueService.addProducer(PRODUCER_ID);
        queueService.addMessage(PRODUCER_ID, messages[0]);
        queueService.addMessage(PRODUCER_ID, messages[1]);

        assertThat(queueService.getMessages()).containsExactly(messages[0], messages[1]);
    }

    @Test
    public void addMessage_setsDestinationToThis() {
        queueService.addProducer(PRODUCER_ID);
        queueService.addMessage(PRODUCER_ID, messages[0]);
        queueService.addMessage(PRODUCER_ID, messages[1]);

        assertThat(queueService.getMessages())
                .extracting(Message::getDestination)
                .containsExactly(new Destination(DestinationType.QUEUE, ID), new Destination(DestinationType.QUEUE, ID));
    }

    @Test
    public void addMessage_setsMessageId() {
        queueService.addProducer(PRODUCER_ID);
        queueService.addConsumer(CONSUMER_ID_1);
        queueService.addConsumer(CONSUMER_ID_2);

        queueService.addMessage(PRODUCER_ID, messages[0]);
        queueService.addMessage(PRODUCER_ID, messages[1]);

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
    public void deliverMessage_consumerDoesNotExist_throwsConsumerDoesNotExist() {
        queueService.addProducer(PRODUCER_ID);
        queueService.addMessage(PRODUCER_ID, messages[0]);

        assertThatExceptionOfType(ConsumerDoesNotExistException.class)
                .isThrownBy(() -> queueService.deliverMessage(UUID.randomUUID()));

        assertThat(queueService.getConsumers()).isEmpty();
        assertThat(queueService.getMessages()).containsExactly(messages[0]);
        assertThat(queueService.getProducers()).containsOnly(PRODUCER_ID);
    }

    @Test
    public void deliverMessage_consumerExistsNoMessages_returnsEmpty() {
        queueService.addConsumer(CONSUMER_ID);

        Optional<Message> read = queueService.deliverMessage(CONSUMER_ID);

        assertThat(read).isEmpty();
        assertThat(queueService.getConsumers()).containsExactly(CONSUMER_ID);
        assertThat(queueService.getProducers()).isEmpty();
        assertThat(queueService.getMessages()).isEmpty();
    }

    @Test
    public void deliverMessage_consumerExistsWithMessages_popsFirst() {
        queueService.addProducer(PRODUCER_ID);
        queueService.addConsumer(CONSUMER_ID);
        queueService.addMessage(PRODUCER_ID, messages[0]);
        queueService.addMessage(PRODUCER_ID, messages[1]);

        Optional<Message> read = queueService.deliverMessage(CONSUMER_ID);

        assertThat(read).contains(messages[0]);
        assertThat(queueService.getConsumers()).containsExactly(CONSUMER_ID);
        assertThat(queueService.getProducers()).containsOnly(PRODUCER_ID);
        assertThat(queueService.getMessages()).containsExactly(messages[1]);
    }

    @Test
    public void deliverMessage_multipleConsumers_popFromSameQueue() {
        queueService.addProducer(PRODUCER_ID);
        queueService.addMessage(PRODUCER_ID, messages[0]);
        queueService.addMessage(PRODUCER_ID, messages[1]);
        queueService.addMessage(PRODUCER_ID, messages[2]);
        queueService.addMessage(PRODUCER_ID, messages[3]);

        queueService.addConsumer(CONSUMER_ID_1);
        queueService.addConsumer(CONSUMER_ID_2);

        Optional<Message> read1 = queueService.deliverMessage(CONSUMER_ID_1);
        Optional<Message> read2 = queueService.deliverMessage(CONSUMER_ID_2);
        Optional<Message> read3 = queueService.deliverMessage(CONSUMER_ID_1);
        Optional<Message> read4 = queueService.deliverMessage(CONSUMER_ID_2);
        Optional<Message> read5 = queueService.deliverMessage(CONSUMER_ID_2);
        Optional<Message> read6 = queueService.deliverMessage(CONSUMER_ID_1);

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