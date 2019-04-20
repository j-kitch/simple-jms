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

    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final String CONSUMER_ID_JSON = "{\"id\": \"" + CONSUMER_ID + "\"}";

    private static final String MESSAGE = "hello world";
    private static final String MESSAGE_JSON = "{\"message\": \"" + MESSAGE + "\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueService queueService;

    @Test
    public void createConsumer_returnsConsumerIdAsJson() throws Exception {
        when(queueService.createConsumer()).thenReturn(CONSUMER_ID);

        mockMvc.perform(post("/queue/consumer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(CONSUMER_ID_JSON));
        verify(queueService).createConsumer();
        verifyNoMoreInteractions(queueService);
    }

    @Test
    public void readMessage_noMessage_returnsEmptyMessage() throws Exception {
        when(queueService.readMessage(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/queue/receive/" + CONSUMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{}"));
        verify(queueService).readMessage(CONSUMER_ID);
        verifyNoMoreInteractions(queueService);
    }

    @Test
    public void readMessage_message_returnsMessage() throws Exception {
        when(queueService.readMessage(any())).thenReturn(Optional.of(MESSAGE));

        mockMvc.perform(post("/queue/receive/" + CONSUMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(MESSAGE_JSON));
        verify(queueService).readMessage(CONSUMER_ID);
        verifyNoMoreInteractions(queueService);
    }

    @Test
    public void sendMessage_addsMessageToQueue() throws Exception {
        mockMvc.perform(post("/queue/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(MESSAGE_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(queueService).addMessage(MESSAGE);
        verifyNoMoreInteractions(queueService);
    }
}