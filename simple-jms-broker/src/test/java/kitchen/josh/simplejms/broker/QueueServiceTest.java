package kitchen.josh.simplejms.broker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
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
    public void addMessage_appendsMessageToMessages() {
        queueService.addMessage(MESSAGES[0]);
        queueService.addMessage(MESSAGES[1]);

        assertThat(queueService.getMessages()).containsExactly(MESSAGES[0], MESSAGES[1]);
    }

    @Test
    public void readMessage_consumerDoesNotExist_returnsEmpty() {
        queueService.addMessage(MESSAGES[0]);

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
        UUID consumerId = queueService.createConsumer();
        queueService.addMessage(MESSAGES[0]);
        queueService.addMessage(MESSAGES[1]);

        Optional<String> read = queueService.readMessage(consumerId);

        assertThat(read).contains(MESSAGES[0]);
        assertThat(queueService.getConsumers()).containsExactly(consumerId);
        assertThat(queueService.getMessages()).containsExactly(MESSAGES[1]);
    }

    @Test
    public void readMessage_multipleConsumers_popFromSameQueue() {
        queueService.addMessage(MESSAGES[0]);
        queueService.addMessage(MESSAGES[1]);
        queueService.addMessage(MESSAGES[2]);
        queueService.addMessage(MESSAGES[3]);
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