package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.MessageModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProducerTest {

    private static final UUID PRODUCER_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.TOPIC, UUID.randomUUID());
    private static final String BROKER_URL = "http://localhost:9999";

    private static final String SEND_URL = BROKER_URL + "/topic/" + DESTINATION.getId() + "/producer/" + PRODUCER_ID + "/send";
    private static final String DELETE_URL = BROKER_URL + "/topic/" + DESTINATION.getId() + "/producer/" + PRODUCER_ID;

    private static final String MESSAGE = "hello world";

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<MessageModel> messageCaptor;

    private Producer producer;

    @Before
    public void setUp() {
        producer = new Producer(BROKER_URL, restTemplate, new ProducerId(DESTINATION, PRODUCER_ID));
    }

    @Test
    public void sendMessage_restTemplateThrows_throws() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class)
                .isThrownBy(() -> producer.sendMessage(MESSAGE));
        verify(restTemplate).postForEntity(eq(SEND_URL), messageCaptor.capture(), eq(Void.class));
        assertThat(messageCaptor.getValue()).isEqualToComparingFieldByField(new MessageModel(MESSAGE));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void sendMessage_restTemplateReturns_returns() {
        producer.sendMessage(MESSAGE);

        verify(restTemplate).postForEntity(eq(SEND_URL), messageCaptor.capture(), eq(Void.class));
        assertThat(messageCaptor.getValue()).isEqualToComparingFieldByField(new MessageModel(MESSAGE));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void close_notifiesBroker() {
        producer.close();

        verify(restTemplate).delete(DELETE_URL);
    }
}