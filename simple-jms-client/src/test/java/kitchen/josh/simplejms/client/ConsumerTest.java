package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerTest {

    private static final String URL = "localhost:8080";
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
        verify(restTemplate).postForEntity(URL, null, Message.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void receiveMessage_noMessage_returnsEmpty() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new Message(null)));

        Optional<String> message = consumer.receiveMessage();

        assertThat(message).isEmpty();
        verify(restTemplate).postForEntity(URL, null, Message.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void receiveMessage_messageExists_returnsMessage() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new Message(MESSAGE)));

        Optional<String> message = consumer.receiveMessage();

        assertThat(message).contains(MESSAGE);
        verify(restTemplate).postForEntity(URL, null, Message.class);
        verifyNoMoreInteractions(restTemplate);
    }
}