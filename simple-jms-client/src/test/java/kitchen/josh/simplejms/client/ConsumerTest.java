package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.Message;
import kitchen.josh.simplejms.common.MessageModel;
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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerTest {

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.TOPIC, DESTINATION_ID);
    private static final String BROKER_URL = "http://localhost:8080";

    private static final UUID CONSUMER_ID = UUID.randomUUID();

    private static final String RECEIVE_URL = BROKER_URL + "/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive";
    private static final String DELETE_URL = BROKER_URL + "/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID;

    private static final String MESSAGE = "hello world";

    @Mock
    private RestTemplate restTemplate;

    private Consumer consumer;

    @Before
    public void setUp() {
        consumer = new Consumer(BROKER_URL, restTemplate, new ConsumerId(DESTINATION, CONSUMER_ID));
    }

    @Test
    public void receiveMessage_restTemplateThrows_throwsException() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class)
                .isThrownBy(() -> consumer.receiveMessage());
        verify(restTemplate).postForEntity(RECEIVE_URL, null, MessageModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void receiveMessage_noMessage_returnsEmpty() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new MessageModel(emptyList(), null)));

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).isEmpty();
        verify(restTemplate).postForEntity(RECEIVE_URL, null, MessageModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void receiveMessage_messageExists_returnsMessage() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new MessageModel(emptyList(), MESSAGE)));

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).get().isEqualToComparingFieldByFieldRecursively(new Message(DESTINATION, MESSAGE));
        verify(restTemplate).postForEntity(RECEIVE_URL, null, MessageModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void close_notifiesBroker() {
        consumer.close();

        verify(restTemplate).delete(DELETE_URL);
    }
}