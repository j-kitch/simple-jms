package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
import kitchen.josh.simplejms.broker.IdModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SessionTest {

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, DESTINATION_ID);

    private static final String HOST = "localhost:8080";
    private static final String PRODUCER_URL = HOST + "/" + DESTINATION.getType().name().toLowerCase() + "/send";
    private static final String CREATE_CONSUMER_URL = HOST + "/" +  DESTINATION.getType().name().toLowerCase() + "/consumer";
    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final String CONSUMER_URL = HOST + "/" + DESTINATION.getType().name().toLowerCase() + "/receive/" + CONSUMER_ID;

    @Mock
    private RestTemplate restTemplate;

    private Session session;

    @Before
    public void setUp() {
        session = new Session(HOST, restTemplate);
    }

    @Test
    public void createDestination_queue_createsQueueWithId() {
        UUID destinationId = UUID.randomUUID();
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new IdModel(destinationId)));

        Destination destination = session.createDestination(DestinationType.QUEUE);

        assertThat(destination).isEqualToComparingFieldByField(new Destination(DestinationType.QUEUE, destinationId));
        verify(restTemplate).postForEntity(HOST + "/queue", null, IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createDestination_topic_createsTopicWithId() {
        UUID destinationId = UUID.randomUUID();
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new IdModel(destinationId)));

        Destination destination = session.createDestination(DestinationType.TOPIC);

        assertThat(destination).isEqualToComparingFieldByField(new Destination(DestinationType.TOPIC, destinationId));
        verify(restTemplate).postForEntity(HOST + "/topic", null, IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createProducer_createsProducerWithCorrectUrl() {
        UUID producerId = UUID.randomUUID();
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new IdModel(producerId)));

        Producer producer = session.createProducer(DESTINATION);

        assertThat(producer).isEqualToComparingFieldByFieldRecursively(new Producer(HOST, restTemplate, new ProducerId(DESTINATION, producerId)));
        verify(restTemplate).postForEntity(HOST + "/queue/" + DESTINATION_ID + "/producer", null, IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createConsumer_restTemplateThrows_throws() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class)
                .isThrownBy(() -> session.createConsumer(DESTINATION));
        verify(restTemplate).postForEntity(CREATE_CONSUMER_URL, null, IdModel.class);
    }

    @Test
    public void createConsumer_restTemplateReturnsId_returnsConsumerUsingId() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new IdModel(CONSUMER_ID)));

        Consumer consumer = session.createConsumer(DESTINATION);

        assertThat(consumer).isEqualToComparingFieldByField(new Consumer(DESTINATION, CONSUMER_URL, restTemplate));
        verify(restTemplate).postForEntity(CREATE_CONSUMER_URL, null, IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }
}