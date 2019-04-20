package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.MessageModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProducerTest {

    private static final Destination DESTINATION = Destination.TOPIC;
    private static final String URL = "localhost:8080/topic/send";
    private static final String MESSAGE = "hello world";

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<MessageModel> messageCaptor;

    private Producer producer;

    @Before
    public void setUp() {
        producer = new Producer(DESTINATION, URL, restTemplate);
    }

    @Test
    public void sendMessage_restTemplateThrows_throws() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class)
                .isThrownBy(() -> producer.sendMessage(MESSAGE));
        verify(restTemplate).postForEntity(eq(URL), messageCaptor.capture(), eq(Void.class));
        assertThat(messageCaptor.getValue()).isEqualToComparingFieldByField(new MessageModel(MESSAGE));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void sendMessage_restTemplateReturns_returns() {
        producer.sendMessage(MESSAGE);

        verify(restTemplate).postForEntity(eq(URL), messageCaptor.capture(), eq(Void.class));
        assertThat(messageCaptor.getValue()).isEqualToComparingFieldByField(new MessageModel(MESSAGE));
        verifyNoMoreInteractions(restTemplate);
    }
}