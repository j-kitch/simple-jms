package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.ConsumerId;
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

    private static final String HOST = "localhost:8080";
    private static final String PRODUCER_URL = HOST + "/producer";
    private static final String CREATE_CONSUMER_URL = HOST + "/consumer";
    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final String CONSUMER_URL = HOST + "/consumer/" + CONSUMER_ID;

    @Mock
    private RestTemplate restTemplate;

    private Session session;

    @Before
    public void setUp() {
        session = new Session(HOST, restTemplate);
    }

    @Test
    public void createProducer_createsProducerWithCorrectUrl() {
        Producer producer = session.createProducer();

        assertThat(producer).isEqualToComparingFieldByField(new Producer(PRODUCER_URL, restTemplate));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createConsumer_restTemplateThrows_throws() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class)
                .isThrownBy(() -> session.createConsumer());
        verify(restTemplate).postForEntity(CREATE_CONSUMER_URL, null, ConsumerId.class);
    }

    @Test
    public void createConsumer_restTemplateReturnsId_returnsConsumerUsingId() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new ConsumerId(CONSUMER_ID)));

        Consumer consumer = session.createConsumer();

        assertThat(consumer).isEqualToComparingFieldByField(new Consumer(CONSUMER_URL, restTemplate));
        verify(restTemplate).postForEntity(CREATE_CONSUMER_URL, null, ConsumerId.class);
        verifyNoMoreInteractions(restTemplate);
    }
}