package kitchen.josh.simplejms.broker;

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
    private static final String MESSAGE = "hello world";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SingleDestinationService singleDestinationService;

    @MockBean
    private DestinationService destinationService;

    @Test
    public void createDestination_topic_returnsOkAndId() throws Exception {
        when(destinationService.createDestination(any())).thenReturn(DESTINATION_ID);

        mockMvc.perform(post("/topic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + DESTINATION_ID + "\"}"));

        verify(destinationService).createDestination(DestinationType.TOPIC);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createDestination_queue_returnsOkAndId() throws Exception {
        when(destinationService.createDestination(any())).thenReturn(DESTINATION_ID);

        mockMvc.perform(post("/queue"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + DESTINATION_ID + "\"}"));

        verify(destinationService).createDestination(DestinationType.QUEUE);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createConsumer_returnsOkAndId() throws Exception {
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.createConsumer()).thenReturn(CONSUMER_ID);

        mockMvc.perform(post("/queue/" + DESTINATION_ID + "/consumer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + CONSUMER_ID + "\"}"));

        verify(destinationService).findDestination(DestinationType.QUEUE, DESTINATION_ID);
        verify(singleDestinationService).createConsumer();
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createProducer_returnsOkAndId() throws Exception {
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.createProducer()).thenReturn(PRODUCER_ID);

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/producer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + PRODUCER_ID + "\"}"));

        verify(destinationService).findDestination(DestinationType.TOPIC, DESTINATION_ID);
        verify(singleDestinationService).createProducer();
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void deleteConsumer_returnsOk() throws Exception {
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));

        mockMvc.perform(delete("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID))
                .andExpect(status().isOk());

        verify(destinationService).findDestination(DestinationType.TOPIC, DESTINATION_ID);
        verify(singleDestinationService).removeConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void deleteProducer_returnsOk() throws Exception {
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));

        mockMvc.perform(delete("/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_ID))
                .andExpect(status().isOk());

        verify(destinationService).findDestination(DestinationType.QUEUE, DESTINATION_ID);
        verify(singleDestinationService).removeProducer(PRODUCER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void sendMessage_returnsOk() throws Exception {
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));

        mockMvc.perform(post("/queue/" + DESTINATION_ID + "/producer/" + PRODUCER_ID + "/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"message\": \"" + MESSAGE + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(destinationService).findDestination(DestinationType.QUEUE, DESTINATION_ID);
        verify(singleDestinationService).addMessage(PRODUCER_ID, MESSAGE);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void receiveMessage_noMessage_returnsNull() throws Exception {
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.readMessage(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": null}"));

        verify(destinationService).findDestination(DestinationType.TOPIC, DESTINATION_ID);
        verify(singleDestinationService).readMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void receiveMessage_message_returnsMessage() throws Exception {
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.readMessage(any())).thenReturn(Optional.of(MESSAGE));

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"" + MESSAGE + "\"}"));

        verify(destinationService).findDestination(DestinationType.TOPIC, DESTINATION_ID);
        verify(singleDestinationService).readMessage(CONSUMER_ID);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }
}