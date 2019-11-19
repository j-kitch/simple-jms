package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.MessageFactory;
import kitchen.josh.simplejms.common.message.MessageModel;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.body.TextBodyModel;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.headers.HeadersModel;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProducerController.class)
public class ProducerControllerIntegrationTest {

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final UUID PRODUCER_ID = UUID.randomUUID();
    private static final String TEXT = "hello world";
    private static final Message MESSAGE = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody(TEXT));

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SingleDestinationService singleDestinationService;

    @MockBean
    private ProducerService producerService;

    @MockBean
    private MessageFactory messageFactory;

    @Test
    public void createProducer_returnsOkAndId() throws Exception {
        when(producerService.createProducer(any())).thenReturn(PRODUCER_ID);

        mockMvc.perform(post("/producer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"destination\": \"topic:" + DESTINATION_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\": \"" + PRODUCER_ID + "\"}", true));

        verify(producerService).createProducer(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(producerService, singleDestinationService, messageFactory);
    }

    @Test
    public void createProducer_unknownDestinationType_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/producer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"destination\": \"abcd\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\": \"Malformed JSON\"}", true));

        verifyZeroInteractions(producerService, singleDestinationService, messageFactory);
    }

    @Test
    public void createProducer_destinationDoesNotExist_returnsBadRequest() throws Exception {
        when(producerService.createProducer(any())).thenThrow(DestinationDoesNotExistException.class);

        mockMvc.perform(post("/producer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"destination\": \"queue:" + DESTINATION_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Failed to create producer: the destination does not exist\"}", true));

        verify(producerService).createProducer(new Destination(DestinationType.QUEUE, DESTINATION_ID));
        verifyNoMoreInteractions(producerService, singleDestinationService, messageFactory);
    }

    @Test
    public void deleteProducer_returnsOk() throws Exception {
        mockMvc.perform(delete("/producer/" + PRODUCER_ID))
                .andExpect(status().isOk());

        verify(producerService).removeProducer(PRODUCER_ID);
        verifyNoMoreInteractions(producerService, singleDestinationService, messageFactory);
    }

    @Test
    public void deleteProducer_producerDoesNotExist_returnsBadRequest() throws Exception {
        doThrow(ProducerDoesNotExistException.class).when(producerService).removeProducer(PRODUCER_ID);

        mockMvc.perform(delete("/producer/" + PRODUCER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Failed to delete producer: the producer does not exist\"}", true));

        verify(producerService).removeProducer(PRODUCER_ID);
        verifyNoMoreInteractions(producerService, singleDestinationService, messageFactory);
    }

    @Test
    public void sendMessage_returnsOk() throws Exception {
        when(messageFactory.create(any())).thenReturn(MESSAGE);

        mockMvc.perform(post("/producer/" + PRODUCER_ID + "/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"body\": {\"type\": \"text\", \"text\": \"" + TEXT + "\"}, \"properties\": [], \"headers\": {}}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(producerService).sendMessage(PRODUCER_ID, MESSAGE);
        verify(messageFactory).create(new MessageModel(new HeadersModel(null, null), emptyList(), new TextBodyModel(TEXT)));
        verifyNoMoreInteractions(producerService, singleDestinationService, messageFactory);
    }

    @Test
    public void sendMessage_producerDoesNotExist_returnsBadRequest() throws Exception {
        when(messageFactory.create(any())).thenReturn(MESSAGE);
        doThrow(ProducerDoesNotExistException.class).when(producerService).sendMessage(any(), any());

        mockMvc.perform(post("/producer/" + PRODUCER_ID + "/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"body\": {\"type\": \"text\", \"text\": \"" + TEXT + "\"}, \"properties\": [], \"headers\": {}}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Failed to send message: the producer does not exist\"}", true));

        verify(messageFactory).create(new MessageModel(new HeadersModel(null, null), emptyList(), new TextBodyModel(TEXT)));
        verify(producerService).sendMessage(PRODUCER_ID, MESSAGE);
        verifyNoMoreInteractions(producerService, singleDestinationService, messageFactory);
    }
}
