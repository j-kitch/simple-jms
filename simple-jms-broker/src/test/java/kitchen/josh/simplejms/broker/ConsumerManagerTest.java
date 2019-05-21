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
public class ConsumerManagerTest {

    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.TOPIC, UUID.randomUUID());

    @Mock
    private SingleDestinationService singleDestinationService;

    @Mock
    private DestinationService destinationService;

    private ConsumerManager consumerManager;

    @Before
    public void setUp() {
        consumerManager = new ConsumerManager(destinationService);
    }

    @Test
    public void createConsumer() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));

        UUID consumerId = consumerManager.createConsumer(DESTINATION);

        assertThat(consumerId).isNotNull();
        verify(destinationService).findDestination(DESTINATION);
        verify(singleDestinationService).addConsumer(consumerId);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createConsumer_destinationDoesNotExist_throwsDestinationDoesNotExist() {
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(DestinationDoesNotExistException.class)
                .isThrownBy(() -> consumerManager.createConsumer(DESTINATION));

        verify(destinationService).findDestination(DESTINATION);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void findConsumer_consumerDoesNotExist_returnsEmpty() {
        assertThat(consumerManager.findConsumer(CONSUMER_ID)).isEmpty();
        verifyZeroInteractions(destinationService);
    }

    @Test
    public void findConsumer_consumerExists_returnsConsumer() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        UUID consumerId = consumerManager.createConsumer(DESTINATION);
        reset(destinationService);

        Optional<SingleConsumerService> consumerService = consumerManager.findConsumer(consumerId);

        assertThat(consumerService).get().isEqualToComparingFieldByField(new SingleConsumerService(consumerId, singleDestinationService));
        verifyZeroInteractions(destinationService);
    }

    @Test
    public void removeConsumer_consumerDoesNotExist_throwsConsumerDoesNotExist() {
        assertThatExceptionOfType(ConsumerDoesNotExistException.class)
                .isThrownBy(() -> consumerManager.removeConsumer(CONSUMER_ID));

        verifyZeroInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void removeConsumer_consumerExists_removesFromDestination() {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        UUID consumerId = consumerManager.createConsumer(DESTINATION);
        reset(singleDestinationService);

        consumerManager.removeConsumer(consumerId);

        verify(singleDestinationService).removeConsumer(consumerId);
        verify(destinationService, atLeastOnce()).findDestination(DESTINATION);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }
}