package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationModel;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.IdModel;
import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.MessageModelFactory;
import kitchen.josh.simplejms.common.message.ObjectMessage;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.BodyFactory;
import kitchen.josh.simplejms.common.message.body.BodyModelFactory;
import kitchen.josh.simplejms.common.message.body.ObjectBody;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.HeadersFactory;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.headers.HeadersModelFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesFactory;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import kitchen.josh.simplejms.common.message.properties.PropertyModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SessionTest {

    private static final String HOST = "localhost:8080";

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, DESTINATION_ID);

    private static final ConsumerId CONSUMER_ID = new ConsumerId(DESTINATION, UUID.randomUUID());
    private static final ProducerId PRODUCER_ID = new ProducerId(DESTINATION, UUID.randomUUID());

    private static final String TEXT = "hello world";
    private static final TextMessage TEXT_MESSAGE = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody(TEXT));

    private static final Serializable OBJECT = 10;
    private static final ObjectMessage OBJECT_MESSAGE = new ObjectMessage(new HeadersImpl(), new PropertiesImpl(), new ObjectBody(OBJECT));


    private static final MessageFactory MESSAGE_FACTORY = new MessageFactory(new HeadersFactory(), new PropertiesFactory(), new BodyFactory());
    private static final MessageModelFactory MESSAGE_MODEL_FACTORY = new MessageModelFactory(new HeadersModelFactory(), new PropertyModelFactory(), new BodyModelFactory());

    @Mock
    private RestTemplate restTemplate;

    private Session session;

    @Before
    public void setUp() {
        session = new Session(HOST, restTemplate);
    }

    @Test
    public void createDestination_queue_createsQueueWithId() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new IdModel(DESTINATION_ID)));

        Destination destination = session.createDestination(DestinationType.QUEUE);

        assertThat(destination).isEqualTo(DESTINATION);
        verify(restTemplate).postForEntity(HOST + "/queue", null, IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createProducer_createsProducerWithCorrectUrl() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new IdModel(PRODUCER_ID.getId())));

        Producer producer = session.createProducer(DESTINATION);

        assertThat(producer).isEqualToComparingFieldByFieldRecursively(new Producer(HOST, restTemplate, PRODUCER_ID, MESSAGE_MODEL_FACTORY));
        verify(restTemplate).postForEntity(HOST + "/queue/" + DESTINATION_ID + "/producer", null, IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createConsumer_restTemplateThrows_throws() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class).isThrownBy(() -> session.createConsumer(DESTINATION));
        verify(restTemplate).postForEntity(HOST + "/consumer", new DestinationModel(DESTINATION), IdModel.class);
    }

    @Test
    public void createConsumer_restTemplateReturnsId_returnsConsumerUsingId() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new IdModel(CONSUMER_ID.getId())));

        Consumer consumer = session.createConsumer(DESTINATION);

        assertThat(consumer).isEqualToComparingFieldByFieldRecursively(new Consumer(HOST, restTemplate, CONSUMER_ID, MESSAGE_FACTORY));
        verify(restTemplate).postForEntity(HOST + "/consumer", new DestinationModel(DESTINATION), IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createTextMessage_createsEmptyTextMessage() {
        TextMessage message = session.createTextMessage();

        assertThat(message).isEqualToComparingFieldByFieldRecursively(new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody()));
    }

    @Test
    public void createTextMessage_text_createsTextMessageWithText() {
        TextMessage message = session.createTextMessage(TEXT);

        assertThat(message).isEqualToComparingFieldByFieldRecursively(TEXT_MESSAGE);
    }

    @Test
    public void createObjectMessage_createsEmptyObjectMessage() {
        ObjectMessage message = session.createObjectMessage();

        assertThat(message).isEqualToComparingFieldByFieldRecursively(new ObjectMessage(new HeadersImpl(), new PropertiesImpl(), new ObjectBody()));
    }

    @Test
    public void createObjectBody_object_createsObjectMessageWithObject() {
        ObjectMessage message = session.createObjectMessage(10);

        assertThat(message).isEqualToComparingFieldByFieldRecursively(OBJECT_MESSAGE);
    }
}