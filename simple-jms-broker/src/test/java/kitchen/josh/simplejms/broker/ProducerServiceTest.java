package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.ObjectMessage;
import kitchen.josh.simplejms.common.message.body.ObjectBody;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProducerServiceTest {

    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, UUID.randomUUID());
    private static final Message MESSAGE = new ObjectMessage(new HeadersImpl(), new PropertiesImpl(), new ObjectBody());

    @Mock
    private DestinationService destinationService;

    @Mock
    private SingleDestinationService singleDestinationService;

    private ProducerService producerService;

    @Before
    public void setUp() {
        producerService = new ProducerService(destinationService);
    }

    @Test
    public void createProducer_destinationDoesNotExist_throwsDestinationDoesNotExist() {
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(DestinationDoesNotExistException.class)
                .isThrownBy(() -> producerService.createProducer(DESTINATION));

        verify(destinationService).findDestination(DESTINATION);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createProducer_createsProducer() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));

        UUID producerId = producerService.createProducer(DESTINATION);

        verify(destinationService).findDestination(DESTINATION);
        verify(singleDestinationService).addProducer(producerId);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void sendMessage_producerDoesNotExist_throwsProducerDoesNotExist() {
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> producerService.sendMessage(UUID.randomUUID(), MESSAGE));

        verifyZeroInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void sendMessage_producerExists_sendsMessage() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        UUID producerId = producerService.createProducer(DESTINATION);
        reset(singleDestinationService);

        producerService.sendMessage(producerId, MESSAGE);

        verify(singleDestinationService).addMessage(producerId, MESSAGE);
        verify(destinationService, atLeastOnce()).findDestination(DESTINATION);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void removeProducer_producerDoesNotExist_throwsProducerDoesNotExist() {
        assertThatExceptionOfType(ProducerDoesNotExistException.class)
                .isThrownBy(() -> producerService.removeProducer(UUID.randomUUID()));

        verifyZeroInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void removeProducer_producerExists_removesProducer() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        UUID producerId = producerService.createProducer(DESTINATION);
        reset(singleDestinationService);

        producerService.removeProducer(producerId);

        verify(singleDestinationService).removeProducer(producerId);
        verify(destinationService, atLeastOnce()).findDestination(DESTINATION);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }
}