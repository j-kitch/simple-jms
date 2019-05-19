package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.common.*;
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

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, DESTINATION_ID);

    private static final String HOST = "localhost:8080";
    private static final UUID CONSUMER_ID = UUID.randomUUID();

    private static final String TEXT = "hello world";
    private static final TextMessage TEXT_MESSAGE = new TextMessage(new PropertiesImpl(), new TextBody(TEXT));

    private static final Serializable OBJECT = 10;
    private static final ObjectMessage OBJECT_MESSAGE = new ObjectMessage(new PropertiesImpl(), new ObjectBody(OBJECT));

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

        assertThat(producer).isEqualToComparingFieldByFieldRecursively(new Producer(HOST, restTemplate, new ProducerId(DESTINATION, producerId), new MessageModelFactory(new PropertyModelFactory(), new BodyModelFactory())));
        verify(restTemplate).postForEntity(HOST + "/queue/" + DESTINATION_ID + "/producer", null, IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createConsumer_restTemplateThrows_throws() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RestClientException.class);

        assertThatExceptionOfType(RestClientException.class)
                .isThrownBy(() -> session.createConsumer(DESTINATION));
        verify(restTemplate).postForEntity(HOST + "/queue/" + DESTINATION_ID + "/consumer", null, IdModel.class);
    }

    @Test
    public void createConsumer_restTemplateReturnsId_returnsConsumerUsingId() {
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok(new IdModel(CONSUMER_ID)));

        Consumer consumer = session.createConsumer(DESTINATION);

        assertThat(consumer).isEqualToComparingFieldByFieldRecursively(new Consumer(HOST, restTemplate, new ConsumerId(DESTINATION, CONSUMER_ID), new MessageFactory(new PropertiesFactory(), new BodyFactory())));
        verify(restTemplate).postForEntity(HOST + "/queue/" + DESTINATION_ID + "/consumer", null, IdModel.class);
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void createTextMessage_createsEmptyTextMessage() {
        TextMessage message = session.createTextMessage();

        assertThat(message).isEqualToComparingFieldByFieldRecursively(
                new TextMessage(new PropertiesImpl(), new TextBody()));
    }

    @Test
    public void createTextMessage_text_createsTextMessageWithText() {
        TextMessage message = session.createTextMessage(TEXT);

        assertThat(message).isEqualToComparingFieldByFieldRecursively(TEXT_MESSAGE);
    }

    @Test
    public void createObjectMessage_createsEmptyObjectMessage() {
        ObjectMessage message = session.createObjectMessage();

        assertThat(message).isEqualToComparingFieldByFieldRecursively(
                new ObjectMessage(new PropertiesImpl(), new ObjectBody()));
    }

    @Test
    public void createObjectBody_object_createsObjectMessageWithObject() {
        ObjectMessage message = session.createObjectMessage(10);

        assertThat(message).isEqualToComparingFieldByFieldRecursively(OBJECT_MESSAGE);
    }
}