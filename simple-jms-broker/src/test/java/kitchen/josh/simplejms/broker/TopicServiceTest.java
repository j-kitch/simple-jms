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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TopicServiceTest {

    private static final UUID ID = UUID.randomUUID();
    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final UUID CONSUMER_ID_1 = UUID.randomUUID();
    private static final UUID CONSUMER_ID_2 = UUID.randomUUID();
    private static final UUID PRODUCER_ID = UUID.randomUUID();
    private static final UUID PRODUCER_ID_1 = UUID.randomUUID();
    private static final UUID PRODUCER_ID_2 = UUID.randomUUID();

    private Message[] messages;

    private TopicService topicService;

    @Before
    public void setUp() {
        messages = createMessages();
        topicService = new TopicService(ID);
    }

    @Test
    public void addConsumer_createsQueueForConsumer() {
        topicService.addConsumer(ID);

        assertThat(topicService.getConsumerQueues()).containsOnlyKeys(ID);
        assertThat(topicService.getConsumerQueues().get(ID)).isEmpty();
        assertThat(topicService.getProducers()).isEmpty();
    }

    @Test
    public void addConsumer_consumerAlreadyExists_throwsIllegalState() {
        topicService.addConsumer(ID);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> topicService.addConsumer(ID));
    }

    @Test
    public void addProducer_addsToProducers() {
        topicService.addProducer(ID);

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).containsExactly(ID);
    }

    @Test
    public void addProducer_producerAlreadyExists_throwsIllegalState() {
        topicService.addProducer(ID);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> topicService.addProducer(ID));
    }

    @Test
    public void removeConsumer_removesConsumerAndQueue() {
        topicService.addConsumer(CONSUMER_ID);
        topicService.addProducer(PRODUCER_ID);
        topicService.addMessage(PRODUCER_ID, messages[0]);
        topicService.addMessage(PRODUCER_ID, messages[1]);

        topicService.removeConsumer(CONSUMER_ID);

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
        topicService.addProducer(PRODUCER_ID);

        topicService.removeProducer(PRODUCER_ID);

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
        topicService.addProducer(PRODUCER_ID);
        topicService.addMessage(PRODUCER_ID, messages[0]);

        assertThat(topicService.getConsumerQueues()).isEmpty();
        assertThat(topicService.getProducers()).containsOnly(PRODUCER_ID);
    }

    @Test
    public void addMessage_producerDoesNotExist_consumersExist_throwsProducerDoesNotExist() {
        topicService.addConsumer(CONSUMER_ID_1);
        topicService.addConsumer(CONSUMER_ID_2);

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
        topicService.addProducer(PRODUCER_ID);
        topicService.addConsumer(CONSUMER_ID_1);
        topicService.addConsumer(CONSUMER_ID_2);

        topicService.addMessage(PRODUCER_ID, messages[0]);
        topicService.addMessage(PRODUCER_ID, messages[1]);

        assertThat(topicService.getConsumerQueues()).hasSize(2);
        topicService.getConsumerQueues().values().forEach(queue -> {
            assertThat(queue).containsExactly(messages[0], messages[1]);
        });
        assertThat(topicService.getProducers()).containsOnly(PRODUCER_ID);
    }

    @Test
    public void addMessage_setsDestinationToThis() {
        topicService.addProducer(PRODUCER_ID);
        topicService.addConsumer(CONSUMER_ID_1);
        topicService.addConsumer(CONSUMER_ID_2);

        topicService.addMessage(PRODUCER_ID, messages[0]);
        topicService.addMessage(PRODUCER_ID, messages[1]);

        topicService.getConsumerQueues().values().forEach(queue ->
                assertThat(queue)
                        .extracting(Message::getDestination)
                        .containsExactly(new Destination(DestinationType.TOPIC, ID), new Destination(DestinationType.TOPIC, ID)));
    }

    @Test
    public void addMessage_setsMessageId() {
        topicService.addProducer(PRODUCER_ID);
        topicService.addConsumer(CONSUMER_ID_1);
        topicService.addConsumer(CONSUMER_ID_2);

        topicService.addMessage(PRODUCER_ID, messages[0]);
        topicService.addMessage(PRODUCER_ID, messages[1]);

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
        topicService.addConsumer(CONSUMER_ID);
        Optional<Message> message = topicService.readMessage(CONSUMER_ID);
        assertThat(message).isEmpty();
    }

    @Test
    public void readMessage_consumerExistsWithMessage_returnsAndRemovesMessage() {
        topicService.addProducer(PRODUCER_ID);
        topicService.addConsumer(CONSUMER_ID);

        topicService.addMessage(PRODUCER_ID, messages[0]);
        topicService.addMessage(PRODUCER_ID, messages[1]);

        Optional<Message> message = topicService.readMessage(CONSUMER_ID);

        assertThat(message).contains(messages[0]);
        assertThat(topicService.getConsumerQueues().get(CONSUMER_ID)).containsExactly(messages[1]);
        assertThat(topicService.getProducers()).containsOnly(PRODUCER_ID);
    }

    @Test
    public void readMessage_multipleConsumers_onlyRemovesThisConsumersMessage() {
        topicService.addProducer(PRODUCER_ID);
        topicService.addConsumer(CONSUMER_ID_1);
        topicService.addConsumer(CONSUMER_ID_2);

        topicService.addMessage(PRODUCER_ID, messages[0]);
        topicService.addMessage(PRODUCER_ID, messages[1]);

        Optional<Message> message = topicService.readMessage(CONSUMER_ID_1);

        assertThat(message).contains(messages[0]);
        assertThat(topicService.getConsumerQueues().get(CONSUMER_ID_1)).containsExactly(messages[1]);
        assertThat(topicService.getConsumerQueues().get(CONSUMER_ID_2)).containsExactly(messages[0], messages[1]);
        assertThat(topicService.getProducers()).containsOnly(PRODUCER_ID);
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