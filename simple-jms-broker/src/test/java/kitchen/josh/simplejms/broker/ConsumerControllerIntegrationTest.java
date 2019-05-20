package kitchen.josh.simplejms.broker;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import kitchen.josh.simplejms.common.message.Message;
import kitchen.josh.simplejms.common.message.MessageModel;
import kitchen.josh.simplejms.common.message.MessageModelFactory;
import kitchen.josh.simplejms.common.message.TextMessage;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.body.TextBodyModel;
import kitchen.josh.simplejms.common.message.headers.HeadersImpl;
import kitchen.josh.simplejms.common.message.headers.HeadersModel;
import kitchen.josh.simplejms.common.message.properties.PropertiesImpl;
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

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ConsumerController.class)
public class ConsumerControllerIntegrationTest {

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final String TEXT = "hello world";
    private static final Message MESSAGE = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody(TEXT));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumerService consumerService;

    @MockBean
    private MessageModelFactory messageModelFactory;

    @Test
    public void createConsumer_returnsOkAndId() throws Exception {
        when(consumerService.createConsumer(any())).thenReturn(CONSUMER_ID);

        mockMvc.perform(post("/consumer")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"destination\": \"topic:" + DESTINATION_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\": \"" + CONSUMER_ID + "\"}", true));

        verify(consumerService).createConsumer(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(consumerService, messageModelFactory);
    }

    @Test
    public void createConsumer_invalidDestinationType_returns() throws Exception {
        mockMvc.perform(post("/consumer")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"destination\": \"abcd\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"Malformed JSON\"}"));

        verifyZeroInteractions(consumerService, messageModelFactory);
    }

    @Test
    public void createConsumer_destinationDoesNotExist_returnsBadRequest() throws Exception {
        when(consumerService.createConsumer(any())).thenThrow(DestinationDoesNotExistException.class);

        mockMvc.perform(post("/consumer")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"destination\": \"topic:" + DESTINATION_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"Failed to create consumer: the destination does not exist\"}", true));

        verify(consumerService).createConsumer(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(consumerService, messageModelFactory);
    }

    @Test
    public void deleteConsumer_returnsOk() throws Exception {
        mockMvc.perform(delete("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID))
                .andExpect(status().isOk());

        verify(consumerService).removeConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(consumerService, messageModelFactory);
    }

    @Test
    public void deleteConsumer_consumerDoesNotExist_returnsBadRequest() throws Exception {
        doThrow(ConsumerDoesNotExistException.class).when(consumerService).removeConsumer(any());

        mockMvc.perform(delete("/queue/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"Failed to delete consumer: the consumer does not exist\"}", true));

        verify(consumerService).removeConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(consumerService, messageModelFactory);
    }

    @Test
    public void receiveMessage_noMessage_returnsNull() throws Exception {
        when(consumerService.readMessage(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"body\": null, \"properties\": [], headers: null}", true));

        verify(consumerService).readMessage(CONSUMER_ID);
        verifyNoMoreInteractions(consumerService, messageModelFactory);
    }

    @Test
    public void receiveMessage_message_returnsMessage() throws Exception {
        when(messageModelFactory.create(any())).thenReturn(new MessageModel(new HeadersModel(null, null), emptyList(), new TextBodyModel(TEXT)));
        when(consumerService.readMessage(any())).thenReturn(Optional.of(MESSAGE));

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"body\": {\"type\": \"text\", \"text\": \"" + TEXT + "\"}, \"properties\": []}"));

        verify(consumerService).readMessage(CONSUMER_ID);
        verify(messageModelFactory).create(MESSAGE);
        verifyNoMoreInteractions(consumerService, messageModelFactory);
    }

    @Test
    public void receiveMessage_consumerDoesNotExist_returnsBadRequest() throws Exception {
        when(consumerService.readMessage(any())).thenThrow(ConsumerDoesNotExistException.class);

        mockMvc.perform(post("/topic/" + DESTINATION_ID + "/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"message\": \"Failed to receive message: the consumer does not exist\"}"));

        verify(consumerService).readMessage(CONSUMER_ID);
        verifyNoMoreInteractions(consumerService, messageModelFactory);
    }
}
