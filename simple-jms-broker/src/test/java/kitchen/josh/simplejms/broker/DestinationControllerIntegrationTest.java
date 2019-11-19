package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.DestinationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DestinationController.class)
public class DestinationControllerIntegrationTest {

    private static final UUID DESTINATION_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DestinationService destinationService;

    @Test
    public void createDestination_topic_returnsOkAndId() throws Exception {
        when(destinationService.createDestination(any())).thenReturn(DESTINATION_ID);

        mockMvc.perform(post("/topic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\": \"" + DESTINATION_ID + "\"}", true));

        verify(destinationService).createDestination(DestinationType.TOPIC);
        verifyNoMoreInteractions(destinationService);
    }

    @Test
    public void createDestination_queue_returnsOkAndId() throws Exception {
        when(destinationService.createDestination(any())).thenReturn(DESTINATION_ID);

        mockMvc.perform(post("/queue"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\": \"" + DESTINATION_ID + "\"}", true));

        verify(destinationService).createDestination(DestinationType.QUEUE);
        verifyNoMoreInteractions(destinationService);
    }

    @Test
    public void createDestination_unknownDestinationType_returnsNotFound() throws Exception {
        mockMvc.perform(post("/ooga-booga"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verifyZeroInteractions(destinationService);
    }
}