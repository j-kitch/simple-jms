package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerServiceTest {

    private static final Destination DESTINATION = new Destination(DestinationType.TOPIC, UUID.randomUUID());
    private static final UUID CONSUMER_ID = UUID.randomUUID();

    @Mock
    private SingleDestinationService singleDestinationService;

    @Mock
    private DestinationService destinationService;

    private ConsumerService consumerService;

    @Before
    public void setUp() {
        consumerService = new ConsumerService(destinationService);
    }

    @Test
    public void createConsumer() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));

        UUID consumerId = consumerService.createConsumer(DESTINATION);

        assertThat(consumerId).isNotNull();
        verify(destinationService).findDestination(DESTINATION);
        verify(singleDestinationService).addConsumer(consumerId);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createConsumer_destinationDoesNotExist_throwsDestinationDoesNotExist() {
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(DestinationDoesNotExistException.class).isThrownBy(() -> consumerService.createConsumer(DESTINATION));

        verify(destinationService).findDestination(DESTINATION);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void findConsumerDestination_consumerDoesNotExist_returnsEmpty() {
        assertThat(consumerService.findConsumerDestination(CONSUMER_ID)).isEmpty();

        verifyZeroInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void findConsumerDestination_consumerExists_returnsDestinationService() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        UUID consumerId = consumerService.createConsumer(DESTINATION);
        reset(singleDestinationService);

        Optional<SingleDestinationService> destination = consumerService.findConsumerDestination(consumerId);

        assertThat(destination).contains(singleDestinationService);
        verify(destinationService, atLeastOnce()).findDestination(DESTINATION);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void removeConsumer_consumerDoesNotExist_throwsConsumerDoesNotExist() {
        assertThatExceptionOfType(ConsumerDoesNotExistException.class).isThrownBy(() -> consumerService.removeConsumer(CONSUMER_ID));

        verifyZeroInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void removeConsumer_consumerExists_removesFromDestination() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        UUID consumerId = consumerService.createConsumer(DESTINATION);
        reset(singleDestinationService);

        consumerService.removeConsumer(consumerId);

        verify(singleDestinationService).removeConsumer(consumerId);
        verify(destinationService, atLeastOnce()).findDestination(DESTINATION);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }
}