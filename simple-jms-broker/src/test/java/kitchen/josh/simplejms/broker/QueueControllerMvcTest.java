package kitchen.josh.simplejms.broker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(QueueController.class)
public class QueueControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueService queueService;

    @Test
    public void createConsumer_returnsConsumerIdAsJson() throws Exception {
        UUID consumerId = UUID.randomUUID();

        when(queueService.createConsumer()).thenReturn(consumerId);

        mockMvc.perform(post("/queue/consumer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + consumerId + "\"}"));
        verify(queueService).createConsumer();
        verifyNoMoreInteractions(queueService);
    }

    @Test
    public void readMessage_noMessage_returnsEmptyMessage() throws Exception {
        UUID consumerId = UUID.randomUUID();

        when(queueService.readMessage(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/queue/receive/" + consumerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{}"));
        verify(queueService).readMessage(consumerId);
        verifyNoMoreInteractions(queueService);
    }

    @Test
    public void readMessage_message_returnsMessage() throws Exception {
        UUID consumerId = UUID.randomUUID();
        String message = "hello world";

        when(queueService.readMessage(any())).thenReturn(Optional.of(message));

        mockMvc.perform(post("/queue/receive/" + consumerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"" + message + "\"}"));
        verify(queueService).readMessage(consumerId);
        verifyNoMoreInteractions(queueService);
    }

    @Test
    public void sendMessage_addsMessageToQueue() throws Exception {
        String message = "hello world";

        mockMvc.perform(post("/queue/send")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content("{\"message\": \"" + message + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(queueService).addMessage(message);
        verifyNoMoreInteractions(queueService);
    }
}