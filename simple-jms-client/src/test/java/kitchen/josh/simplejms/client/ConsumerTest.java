package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.*;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.TOPIC, DESTINATION_ID);
    private static final String BROKER_URL = "http://localhost:8080";

    private static final UUID CONSUMER_ID = UUID.randomUUID();

    private static final String RECEIVE_URL = BROKER_URL + "/consumer/" + CONSUMER_ID + "/receive";
    private static final String DELETE_URL = BROKER_URL + "/consumer/" + CONSUMER_ID;
    private static final String ACKNOWLEDGE_URL = BROKER_URL + "/consumer/" + CONSUMER_ID;

    private static final String TEXT = "hello world";
    private static final Message MESSAGE = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody(TEXT));
    private static final MessageModel MESSAGE_MODEL = new MessageModel(null, null, null);

    @Captor
    private ArgumentCaptor<MessageIdModel> messageIdModelCaptor;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MessageFactory messageFactory;

    private Consumer consumer;

    @Before
    public void setUp() {
        consumer = new Consumer(BROKER_URL, restTemplate, new ConsumerId(DESTINATION, CONSUMER_ID), messageFactory);
    }

    @Test
    public void receiveMessage_restTemplateThrows_throwsException() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class).isThrownBy(() -> consumer.receiveMessage());
        verify(restTemplate).postForEntity(RECEIVE_URL, null, MessageModel.class);
        verifyNoMoreInteractions(restTemplate, messageFactory);
    }

    @Test
    public void receiveMessage_noMessage_returnsEmpty() throws Exception {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(MESSAGE_MODEL));
        when(messageFactory.create(any())).thenReturn(null);

        Optional<Message> message = consumer.receiveMessage();

        assertThat(message).isEmpty();
        verify(restTemplate).postForEntity(RECEIVE_URL, null, MessageModel.class);
        verify(messageFactory).create(MESSAGE_MODEL);
        verifyNoMoreInteractions(restTemplate, messageFactory);
    }

    @Test
    public void receiveMessage_messageExists_returnsMessage() throws Exception {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(MESSAGE_MODEL));
        when(messageFactory.create(any())).thenReturn(MESSAGE);

        Optional<Message> received = consumer.receiveMessage();

        assertThat(received).contains(MESSAGE);
        verify(restTemplate).postForEntity(RECEIVE_URL, null, MessageModel.class);
        verify(messageFactory).create(MESSAGE_MODEL);
        verifyNoMoreInteractions(restTemplate, messageFactory);
    }

    @Test
    public void acknowledge_restTemplateThrows_throws() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class).isThrownBy(() -> consumer.acknowledge(MESSAGE));
        verify(restTemplate).postForEntity(eq(ACKNOWLEDGE_URL), messageIdModelCaptor.capture(), eq(Void.class));
        verifyNoMoreInteractions(restTemplate, messageFactory);
        assertThat(messageIdModelCaptor.getValue()).isEqualToComparingFieldByField(new MessageIdModel(MESSAGE.getId()));
    }

    @Test
    public void acknowledge_callsBroker() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok().build());

        consumer.acknowledge(MESSAGE);

        verify(restTemplate).postForEntity(eq(ACKNOWLEDGE_URL), messageIdModelCaptor.capture(), eq(Void.class));
        verifyNoMoreInteractions(restTemplate, messageFactory);
        assertThat(messageIdModelCaptor.getValue()).isEqualToComparingFieldByField(new MessageIdModel(MESSAGE.getId()));
    }

    @Test
    public void close_notifiesBroker() {
        consumer.close();

        verify(restTemplate).delete(DELETE_URL);
        verifyNoMoreInteractions(restTemplate, messageFactory);
    }
}