package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.Message;
import kitchen.josh.simplejms.broker.MessageModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerTest {

    private static final String URL = "localhost:8080/topic/receive/" + UUID.randomUUID();
    private static final String MESSAGE = "hello world";

    @Mock
    private RestTemplate restTemplate;

    private Consumer consumer;

    @Before
    public void setUp() {
        consumer = new Consumer(URL, restTemplate);
    }

    @Test
    public void receiveMessage_restTemplateThrows_throwsException() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class)
                .isThrownBy(() -> consumer.receiveMessage());
        verify(restTemplate).postForEntity(URL, null, MessageModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void receiveMessage_noMessage_returnsEmpty() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new MessageModel(null)));

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).isEmpty();
        verify(restTemplate).postForEntity(URL, null, MessageModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void receiveMessage_messageExists_returnsMessage() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new MessageModel(MESSAGE)));

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).usingFieldByFieldValueComparator().hasValue(new Message(Destination.TOPIC, MESSAGE));
        verify(restTemplate).postForEntity(URL, null, MessageModel.class);
        verifyNoMoreInteractions(restTemplate);
    }
}