package kitchen.josh.simplejms.broker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TopicController.class)
public class TopicControllerMvcTest {

    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final String CONSUMER_ID_JSON = "{\"id\": \"" + CONSUMER_ID + "\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicService topicService;

    @Test
    public void createConsumer_returnsConsumerIdAsJson() throws Exception {
        when(topicService.createConsumer()).thenReturn(CONSUMER_ID);

        mockMvc.perform(post("/consumer"))
                .andExpect(status().isOk())
                .andExpect(content().json(CONSUMER_ID_JSON));
        verify(topicService).createConsumer();
        verifyNoMoreInteractions(topicService);
    }
}
