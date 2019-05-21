package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.ObjectMessage;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.ObjectBody;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SingleConsumerServiceTest {

    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final Message[] ACKNOWLEDGED = createRandomMessages();
    private static final Message[] NEW_MESSAGES = createRandomMessages();
    private static final Message[] UNACKNOWLEDGED = createRandomMessages();

    @Mock
    private SingleDestinationService destinationService;

    private SingleConsumerService consumerService;

    @Before
    public void setUp() {
        consumerService = new SingleConsumerService(CONSUMER_ID, destinationService);
    }

    @Test
    public void receive_noMessage_returnsEmpty() {
        when(destinationService.deliverMessage(any())).thenReturn(Optional.empty());

        assertThat(consumerService.receive()).isEmpty();

        verify(destinationService).deliverMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService);
    }

    @Test
    public void receive_message_returnsMessage() {
        when(destinationService.deliverMessage(any())).thenReturn(Optional.of(NEW_MESSAGES[0]));

        assertThat(consumerService.receive()).contains(NEW_MESSAGES[0]);

        verify(destinationService).deliverMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void recover_unacknowledgedMessages_receivesUnacknowledgedMessagesThenNewMessages() {
        // Previously received 2 messages.
        when(destinationService.deliverMessage(any())).thenReturn(Optional.of(UNACKNOWLEDGED[0]), Optional.of(UNACKNOWLEDGED[1]));
        consumerService.receive();
        consumerService.receive();
        reset(destinationService);

        // Two more messages waiting.
        when(destinationService.deliverMessage(any())).thenReturn(Optional.of(NEW_MESSAGES[0]), Optional.of(NEW_MESSAGES[1]), Optional.empty());

        consumerService.recover();

        // Receives unacknowledged messages, then new messages.
        assertThat(consumerService.receive()).contains(UNACKNOWLEDGED[0]);
        assertThat(consumerService.receive()).contains(UNACKNOWLEDGED[1]);
        assertThat(consumerService.receive()).contains(NEW_MESSAGES[0]);
        assertThat(consumerService.receive()).contains(NEW_MESSAGES[1]);
        assertThat(consumerService.receive()).isEmpty();
        verify(destinationService, times(3)).deliverMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void recover_acknowledgedMessages_receivesUnacknowledgedMessagesThenNewMessages() {
        // Previously received 4 messages.
        when(destinationService.deliverMessage(any())).thenReturn(
                Optional.of(ACKNOWLEDGED[0]), Optional.of(ACKNOWLEDGED[1]),
                Optional.of(UNACKNOWLEDGED[0]), Optional.of(UNACKNOWLEDGED[1]));
        consumerService.receive();
        consumerService.receive();
        consumerService.receive();
        consumerService.receive();
        reset(destinationService);

        // Two more messages to receive.
        when(destinationService.deliverMessage(any())).thenReturn(
                Optional.of(NEW_MESSAGES[0]), Optional.of(NEW_MESSAGES[1]), Optional.empty());

        // Acknowledge the first two messages.
        consumerService.acknowledge(ACKNOWLEDGED[1].getId());

        consumerService.recover();

        // Receives unacknowledged messages, then new messages.
        assertThat(consumerService.receive()).contains(UNACKNOWLEDGED[0]);
        assertThat(consumerService.receive()).contains(UNACKNOWLEDGED[1]);
        assertThat(consumerService.receive()).contains(NEW_MESSAGES[0]);
        assertThat(consumerService.receive()).contains(NEW_MESSAGES[1]);
        assertThat(consumerService.receive()).isEmpty();
        verify(destinationService, times(3)).deliverMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService);
    }

    @Test
    public void acknowledge_noMessages_doesNothing() {
        when(destinationService.deliverMessage(any())).thenReturn(Optional.of(NEW_MESSAGES[0]));

        consumerService.acknowledge("ID:" + UUID.randomUUID());

        assertThat(consumerService.receive()).contains(NEW_MESSAGES[0]);
        verify(destinationService).deliverMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService);
    }

    @Test
    public void acknowledge_unknownMessageId_doesNothing() {
        // Received two messages.
        when(destinationService.deliverMessage(any())).thenReturn(Optional.of(UNACKNOWLEDGED[0]), Optional.of(UNACKNOWLEDGED[1]));
        consumerService.receive();
        consumerService.receive();
        reset(destinationService);

        // Two more messages waiting.
        when(destinationService.deliverMessage(any())).thenReturn(Optional.of(NEW_MESSAGES[0]), Optional.of(NEW_MESSAGES[1]), Optional.empty());

        // Acknowledge an unknown message ID.
        consumerService.acknowledge("ID:" + UUID.randomUUID());

        consumerService.recover();

        // Doesn't impact recover behaviour.
        assertThat(consumerService.receive()).contains(UNACKNOWLEDGED[0]);
        assertThat(consumerService.receive()).contains(UNACKNOWLEDGED[1]);
        assertThat(consumerService.receive()).contains(NEW_MESSAGES[0]);
        assertThat(consumerService.receive()).contains(NEW_MESSAGES[1]);
        assertThat(consumerService.receive()).isEmpty();
        verify(destinationService, times(3)).deliverMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService);
    }

    @Test
    public void recover_noMessages_ignores() {
        when(destinationService.deliverMessage(any())).thenReturn(Optional.of(NEW_MESSAGES[0]));

        consumerService.recover();

        assertThat(consumerService.receive()).contains(NEW_MESSAGES[0]);
        verify(destinationService).deliverMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService);
    }

    @Test
    public void close() {
        consumerService.close();

        verify(destinationService).removeConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService);
    }

    private static Message[] createRandomMessages() {
        Message[] messages = {
                new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody()),
                new ObjectMessage(new HeadersImpl(), new PropertiesImpl(), new ObjectBody()),
                new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody()),
                new ObjectMessage(new HeadersImpl(), new PropertiesImpl(), new ObjectBody()),
        };

        messages[0].setId("ID:" + UUID.randomUUID());
        messages[1].setId("ID:" + UUID.randomUUID());
        messages[2].setId("ID:" + UUID.randomUUID());
        messages[3].setId("ID:" + UUID.randomUUID());

        return messages;
    }
}