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
public class DestinationControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SingleDestinationService singleDestinationService;

    @MockBean
    private DestinationService destinationService;

    @Test
    public void createDestination_topic_returnsOkAndId() throws Exception {
        UUID id = UUID.randomUUID();
        when(destinationService.createDestination(any())).thenReturn(id);

        mockMvc.perform(post("/topic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + id + "\"}"));

        verify(destinationService).createDestination(DestinationType.TOPIC);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createDestination_queue_returnsOkAndId() throws Exception {
        UUID id = UUID.randomUUID();
        when(destinationService.createDestination(any())).thenReturn(id);

        mockMvc.perform(post("/queue"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + id + "\"}"));

        verify(destinationService).createDestination(DestinationType.QUEUE);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createConsumer_returnsOkAndId() throws Exception {
        UUID destinationId = UUID.randomUUID();
        UUID consumerId = UUID.randomUUID();
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.createConsumer()).thenReturn(consumerId);

        mockMvc.perform(post("/queue/" + destinationId + "/consumer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + consumerId + "\"}"));

        verify(destinationService).findDestination(DestinationType.QUEUE, destinationId);
        verify(singleDestinationService).createConsumer();
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void createProducer_returnsOkAndId() throws Exception {
        UUID destinationId = UUID.randomUUID();
        UUID producerId = UUID.randomUUID();
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.createProducer()).thenReturn(producerId);

        mockMvc.perform(post("/topic/" + destinationId + "/producer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + producerId + "\"}"));

        verify(destinationService).findDestination(DestinationType.TOPIC, destinationId);
        verify(singleDestinationService).createProducer();
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void deleteConsumer_returnsOk() throws Exception {
        UUID destinationId = UUID.randomUUID();
        UUID consumerId = UUID.randomUUID();
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));

        mockMvc.perform(delete("/topic/" + destinationId + "/consumer/" + consumerId))
                .andExpect(status().isOk());

        verify(destinationService).findDestination(DestinationType.TOPIC, destinationId);
        verify(singleDestinationService).removeConsumer(consumerId);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void deleteProducer_returnsOk() throws Exception {
        UUID destinationId = UUID.randomUUID();
        UUID producerId = UUID.randomUUID();
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));

        mockMvc.perform(delete("/queue/" + destinationId + "/producer/" + producerId))
                .andExpect(status().isOk());

        verify(destinationService).findDestination(DestinationType.QUEUE, destinationId);
        verify(singleDestinationService).removeProducer(producerId);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void sendMessage_returnsOk() throws Exception {
        UUID destinationId = UUID.randomUUID();
        UUID producerId = UUID.randomUUID();
        String message = "hello world";
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));

        mockMvc.perform(post("/queue/" + destinationId + "/producer/" + producerId + "/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"message\": \"" + message + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(destinationService).findDestination(DestinationType.QUEUE, destinationId);
        verify(singleDestinationService).addMessage(producerId, message);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void receiveMessage_noMessage_returnsNull() throws Exception {
        UUID destinationId = UUID.randomUUID();
        UUID consumerId = UUID.randomUUID();
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.readMessage(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/topic/" + destinationId + "/consumer/" + consumerId + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": null}"));
        verify(destinationService).findDestination(DestinationType.TOPIC, destinationId);
        verify(singleDestinationService).readMessage(consumerId);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }

    @Test
    public void receiveMessage_message_returnsMessage() throws Exception {
        UUID destinationId = UUID.randomUUID();
        UUID consumerId = UUID.randomUUID();
        String message = "hello world";
        when(destinationService.findDestination(any(), any())).thenReturn(Optional.of(singleDestinationService));
        when(singleDestinationService.readMessage(any())).thenReturn(Optional.of(message));

        mockMvc.perform(post("/topic/" + destinationId + "/consumer/" + consumerId + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"" + message + "\"}"));
        verify(destinationService).findDestination(DestinationType.TOPIC, destinationId);
        verify(singleDestinationService).readMessage(consumerId);
        verifyNoMoreInteractions(destinationService, singleDestinationService);
    }
}