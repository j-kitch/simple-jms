package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

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

    private static final String TEXT = "hello world";
    private static final MessageModel MESSAGE_MODEL = new MessageModel(null, null, null);
    private static final Message MESSAGE = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody(TEXT));

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MessageModelFactory messageModelFactory;

    private Producer producer;

    @Before
    public void setUp() {
        producer = new Producer(BROKER_URL, restTemplate, new ProducerId(DESTINATION, PRODUCER_ID), messageModelFactory);
    }

    @Test
    public void sendMessage_restTemplateThrows_throws() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);
        when(messageModelFactory.create(any())).thenReturn(MESSAGE_MODEL);

        assertThatExceptionOfType(RestClientException.class).isThrownBy(() -> producer.sendMessage(MESSAGE));
        verify(messageModelFactory).create(MESSAGE);
        verify(restTemplate).postForEntity(SEND_URL, MESSAGE_MODEL, Void.class);
        verifyNoMoreInteractions(restTemplate, messageModelFactory);
    }

    @Test
    public void sendMessage_restTemplateReturns_returns() {
        when(messageModelFactory.create(any())).thenReturn(MESSAGE_MODEL);

        producer.sendMessage(MESSAGE);

        verify(messageModelFactory).create(MESSAGE);
        verify(restTemplate).postForEntity(SEND_URL, MESSAGE_MODEL, Void.class);
        verifyNoMoreInteractions(restTemplate, messageModelFactory);
    }

    @Test
    public void close_notifiesBroker() {
        producer.close();

        verify(restTemplate).delete(DELETE_URL);
        verifyNoMoreInteractions(restTemplate, messageModelFactory);
    }
}