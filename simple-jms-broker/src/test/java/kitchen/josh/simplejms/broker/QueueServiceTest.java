package kitchen.josh.simplejms.broker;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class QueueServiceTest {

    private static final String[] MESSAGES = {"a", "b", "c", "d"};

    private QueueService queueService;

    @Before
    public void setUp() {
        queueService = new QueueService();
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
    public void removeProducer_removesProducer() {
        UUID producerId = queueService.createProducer();

        queueService.removeProducer(producerId);

        assertThat(queueService.getProducers()).isEmpty();
    }

    @Test
    public void addMessage_unknownProducer_doesNothing() {
        queueService.addMessage(UUID.randomUUID(), MESSAGES[0]);
        queueService.addMessage(UUID.randomUUID(), MESSAGES[1]);

        assertThat(queueService.getMessages()).isEmpty();
    }

    @Test
    public void addMessage_appendsMessageToMessages() {
        UUID producerId = queueService.createProducer();
        queueService.addMessage(producerId, MESSAGES[0]);
        queueService.addMessage(producerId, MESSAGES[1]);

        assertThat(queueService.getMessages()).containsExactly(MESSAGES[0], MESSAGES[1]);
    }

    @Test
    public void readMessage_consumerDoesNotExist_returnsEmpty() {
        UUID producerId = queueService.createProducer();
        queueService.addMessage(producerId, MESSAGES[0]);

        Optional<String> read = queueService.readMessage(UUID.randomUUID());

        assertThat(read).isEmpty();
        assertThat(queueService.getConsumers()).isEmpty();
        assertThat(queueService.getMessages()).containsExactly(MESSAGES[0]);
    }

    @Test
    public void readMessage_consumerExistsNoMessages_returnsEmpty() {
        UUID consumerId = queueService.createConsumer();

        Optional<String> read = queueService.readMessage(consumerId);

        assertThat(read).isEmpty();
        assertThat(queueService.getConsumers()).containsExactly(consumerId);
        assertThat(queueService.getMessages()).isEmpty();
    }

    @Test
    public void readMessage_consumerExistsWithMessages_popsFirst() {
        UUID producerId = queueService.createProducer();
        UUID consumerId = queueService.createConsumer();
        queueService.addMessage(producerId, MESSAGES[0]);
        queueService.addMessage(producerId, MESSAGES[1]);

        Optional<String> read = queueService.readMessage(consumerId);

        assertThat(read).contains(MESSAGES[0]);
        assertThat(queueService.getConsumers()).containsExactly(consumerId);
        assertThat(queueService.getMessages()).containsExactly(MESSAGES[1]);
    }

    @Test
    public void readMessage_multipleConsumers_popFromSameQueue() {
        UUID producerId = queueService.createProducer();
        queueService.addMessage(producerId, MESSAGES[0]);
        queueService.addMessage(producerId, MESSAGES[1]);
        queueService.addMessage(producerId, MESSAGES[2]);
        queueService.addMessage(producerId, MESSAGES[3]);
        UUID consumerId1 = queueService.createConsumer();
        UUID consumerId2 = queueService.createConsumer();

        Optional<String> read1 = queueService.readMessage(consumerId1);
        Optional<String> read2 = queueService.readMessage(consumerId2);
        Optional<String> read3 = queueService.readMessage(consumerId1);
        Optional<String> read4 = queueService.readMessage(consumerId2);
        Optional<String> read5 = queueService.readMessage(consumerId2);
        Optional<String> read6 = queueService.readMessage(consumerId1);

        assertThat(read1).contains(MESSAGES[0]);
        assertThat(read2).contains(MESSAGES[1]);
        assertThat(read3).contains(MESSAGES[2]);
        assertThat(read4).contains(MESSAGES[3]);
        assertThat(read5).isEmpty();
        assertThat(read6).isEmpty();
    }
}