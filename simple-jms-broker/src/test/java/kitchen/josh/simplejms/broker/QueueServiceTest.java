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
        String message1 = "hello";
        String message2 = "world";

        queueService.addMessage(message1);
        queueService.addMessage(message2);

        assertThat(queueService.getMessages()).containsExactly(message1, message2);
    }

    @Test
    public void readMessage_consumerDoesNotExist_returnsEmpty() {
        String message = "hello";
        queueService.addMessage(message);

        Optional<String> read = queueService.readMessage(UUID.randomUUID());

        assertThat(read).isEmpty();
        assertThat(queueService.getConsumers()).isEmpty();
        assertThat(queueService.getMessages()).containsExactly(message);
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
        String message1 = "hello";
        String message2 = "world";
        queueService.addMessage(message1);
        queueService.addMessage(message2);

        Optional<String> read = queueService.readMessage(consumerId);

        assertThat(read).contains(message1);
        assertThat(queueService.getConsumers()).containsExactly(consumerId);
        assertThat(queueService.getMessages()).containsExactly(message2);
    }

    @Test
    public void readMessage_multipleConsumers_popFromSameQueue() {
        String[] messages = {"a", "b", "c", "d"};
        queueService.addMessage(messages[0]);
        queueService.addMessage(messages[1]);
        queueService.addMessage(messages[2]);
        queueService.addMessage(messages[3]);
        UUID consumerId1 = queueService.createConsumer();
        UUID consumerId2 = queueService.createConsumer();

        Optional<String> read1 = queueService.readMessage(consumerId1);
        Optional<String> read2 = queueService.readMessage(consumerId2);
        Optional<String> read3 = queueService.readMessage(consumerId1);
        Optional<String> read4 = queueService.readMessage(consumerId2);
        Optional<String> read5 = queueService.readMessage(consumerId2);
        Optional<String> read6 = queueService.readMessage(consumerId1);

        assertThat(read1).contains(messages[0]);
        assertThat(read2).contains(messages[1]);
        assertThat(read3).contains(messages[2]);
        assertThat(read4).contains(messages[3]);
        assertThat(read5).isEmpty();
        assertThat(read6).isEmpty();
    }
}