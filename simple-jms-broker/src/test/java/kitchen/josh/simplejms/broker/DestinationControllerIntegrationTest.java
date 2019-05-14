package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DestinationController.class)
public class DestinationControllerIntegrationTest {

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final UUID PRODUCER_ID = UUID.randomUUID();
    private static final String TEXT = "hello world";
    private static final Message MESSAGE = new Message(new Destination(DestinationType.TOPIC, DESTINATION_ID), TEXT);

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SingleDestinationService singleDestinationService;

    @MockBean
    private DestinationService destinationService;

    @MockBean
    private MessageModelFactory messageModelFactory;

    @MockBean
    private MessageFactory messageFactory;

    @Test
    public void createDestination_topic_returnsOkAndId() throws Exception {
        when(destinationService.createDestination(any())).thenReturn(DESTINATION_ID);

        mockMvc.perform(post("/topic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + DESTINATION_ID + "\"}", true));

        verify(destinationService).createDestination(DestinationType.TOPIC);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void createDestination_queue_returnsOkAndId() throws Exception {
        when(destinationService.createDestination(any())).thenReturn(DESTINATION_ID);

        mockMvc.perform(post("/queue"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + DESTINATION_ID + "\"}", true));

        verify(destinationService).createDestination(DestinationType.QUEUE);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void createDestination_unknownDestinationType_returnsNotFound() throws Exception {
        mockMvc.perform(post("/ooga-booga"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
        verifyZeroInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void createConsumer_returnsOkAndId() throws Exception {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.createConsumer()).thenReturn(CONSUMER_ID);

        mockMvc.perform(post("/queue/" + DESTINATION_ID + "/consumer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + CONSUMER_ID + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verify(singleDestinationService).createConsumer();
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void createConsumer_unknownDestinationType_returnsNotFound() throws Exception {
        mockMvc.perform(post("/ooga-booga/" + DESTINATION_ID + "/consumer"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verifyZeroInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void createConsumer_destinationDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to create consumer for topic " + DESTINATION_ID + ": the topic does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void createProducer_returnsOkAndId() throws Exception {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.createProducer()).thenReturn(PRODUCER_ID);

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/producer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + PRODUCER_ID + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verify(singleDestinationService).createProducer();
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void createProducer_unknownDestinationType_returnsNotFound() throws Exception {
        mockMvc.perform(post("/ooga-booga/" + DESTINATION_ID + "/producer"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verifyZeroInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void createProducer_destinationDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to create producer for queue " + DESTINATION_ID + ": the queue does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/queue/" + DESTINATION_ID + "/producer"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void deleteConsumer_returnsOk() throws Exception {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));

        mockMvc.perform(delete("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID))
                .andExpect(status().isOk());

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verify(singleDestinationService).removeConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void deleteConsumer_unknownDestinationType_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/ooga-booga/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verifyZeroInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void deleteConsumer_destinationDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to delete consumer " + CONSUMER_ID + " for topic " + DESTINATION_ID + ": the topic does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void deleteConsumer_consumerDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to delete consumer " + CONSUMER_ID + " for queue " + DESTINATION_ID + ": the consumer does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        doThrow(ConsumerDoesNotExistException.class).when(singleDestinationService).removeConsumer(any());

        mockMvc.perform(delete("/queue/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verify(singleDestinationService).removeConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void deleteProducer_returnsOk() throws Exception {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));

        mockMvc.perform(delete("/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_ID))
                .andExpect(status().isOk());

        verify(destinationService).findDestination(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verify(singleDestinationService).removeProducer(PRODUCER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void deleteProducer_unknownDestinationType_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/ooga-booga/" + DESTINATION_ID + "/producer/" + PRODUCER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verifyZeroInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void deleteProducer_destinationDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to delete producer " + PRODUCER_ID + " for topic " + DESTINATION_ID + ": the topic does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/topic/" + DESTINATION_ID + "/producer/" + PRODUCER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void deleteProducer_producerDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to delete producer " + PRODUCER_ID + " for queue " + DESTINATION_ID + ": the producer does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        doThrow(ProducerDoesNotExistException.class).when(singleDestinationService).removeProducer(any());

        mockMvc.perform(delete("/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verify(singleDestinationService).removeProducer(PRODUCER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void sendMessage_returnsOk() throws Exception {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        when(messageFactory.create(any(), any())).thenReturn(MESSAGE);

        mockMvc.perform(post("/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_ID + "/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"body\": \"" + TEXT + "\", \"properties\": []}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(destinationService).findDestination(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verify(messageFactory).create(new Destination(DestinationType.QUEUE, DESTINATION_ID), new MessageModel(emptyList(), TEXT));
        verify(singleDestinationService).addMessage(PRODUCER_ID, MESSAGE);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void sendMessage_unknownDestinationType_returnsNotFound() throws Exception {
        mockMvc.perform(post("/ooga-booga/" + DESTINATION_ID + "/producer/" + PRODUCER_ID + "/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"body\": \"" + TEXT + "\", \"properties\": []}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verifyZeroInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void sendMessage_destinationDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to send message to topic " + DESTINATION_ID + ": the topic does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/producer/" + PRODUCER_ID + "/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"body\": \"" + TEXT + "\", \"properties\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void sendMessage_producerDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to send message to queue " + DESTINATION_ID + ": the producer " + PRODUCER_ID + " does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        when(messageFactory.create(any(), any())).thenReturn(MESSAGE);
        doThrow(ProducerDoesNotExistException.class).when(singleDestinationService).addMessage(any(), any());

        mockMvc.perform(post("/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_ID + "/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"body\": \"" + TEXT + "\", \"properties\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verify(messageFactory).create(new Destination(DestinationType.QUEUE, DESTINATION_ID), new MessageModel(emptyList(), TEXT));
        verify(singleDestinationService).addMessage(PRODUCER_ID, MESSAGE);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void receiveMessage_noMessage_returnsNull() throws Exception {
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.readMessage(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"body\": null, \"properties\": []}", true));

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verify(singleDestinationService).readMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void receiveMessage_message_returnsMessage() throws Exception {
        Message message = new Message(new Destination(DestinationType.TOPIC, DESTINATION_ID), TEXT);
        when(messageModelFactory.create(any())).thenReturn(new MessageModel(emptyList(), TEXT));
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.readMessage(any())).thenReturn(Optional.of(message));

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"body\": \"" + TEXT + "\", \"properties\": []}"));

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verify(singleDestinationService).readMessage(CONSUMER_ID);
        verify(messageModelFactory).create(message);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void receiveMessage_unknownDestinationType_returnsNotFound() throws Exception {
        mockMvc.perform(post("/ooga-booga/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verifyZeroInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void receiveMessage_destinationDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to receive message: the queue " + DESTINATION_ID + " does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/queue/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}"));

        verify(destinationService).findDestination(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }

    @Test
    public void receiveMessage_consumerDoesNotExist_returnsBadRequest() throws Exception {
        String errorMessage = "Failed to receive message: the consumer " + CONSUMER_ID + " does not exist.";
        when(destinationService.findDestination(any())).thenReturn(Optional.of(singleDestinationService));
        doThrow(ConsumerDoesNotExistException.class).when(singleDestinationService).readMessage(any());

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + errorMessage + "\"}"));

        verify(destinationService).findDestination(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verify(singleDestinationService).readMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService, messageModelFactory, messageFactory);
    }
}