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
@WebMvcTest(ConsumerController.class)
public class ConsumerControllerIntegrationTest {

    private static final UUID DESTINATION_ID = UUID.randomUUID();
    private static final UUID CONSUMER_ID = UUID.randomUUID();
    private static final String TEXT = "hello world";
    private static final Message MESSAGE = new TextMessage(new HeadersImpl(), new PropertiesImpl(), new TextBody(TEXT));
    private static final String MESSAGE_ID = "ID:message-id";

    @Mock
    private SingleConsumerService singleConsumerService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumerManager consumerManager;

    @MockBean
    private MessageModelFactory messageModelFactory;

    @Test
    public void createConsumer_returnsOkAndId() throws Exception {
        when(consumerManager.createConsumer(any())).thenReturn(CONSUMER_ID);

        mockMvc.perform(post("/consumer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"destination\": \"topic:" + DESTINATION_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\": \"" + CONSUMER_ID + "\"}", true));

        verify(consumerManager).createConsumer(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void createConsumer_invalidDestinationType_returns() throws Exception {
        mockMvc.perform(post("/consumer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"destination\": \"abcd\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Malformed JSON\"}"));

        verifyZeroInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void createConsumer_destinationDoesNotExist_returnsBadRequest() throws Exception {
        when(consumerManager.createConsumer(any())).thenThrow(DestinationDoesNotExistException.class);

        mockMvc.perform(post("/consumer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"destination\": \"topic:" + DESTINATION_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Failed to create consumer: the destination does not exist\"}", true));

        verify(consumerManager).createConsumer(new Destination(DestinationType.TOPIC, DESTINATION_ID));
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void deleteConsumer_returnsOk() throws Exception {
        mockMvc.perform(delete("/consumer/" + CONSUMER_ID))
                .andExpect(status().isOk());

        verify(consumerManager).removeConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void deleteConsumer_consumerDoesNotExist_returnsBadRequest() throws Exception {
        doThrow(ConsumerDoesNotExistException.class).when(consumerManager).removeConsumer(any());

        mockMvc.perform(delete("/consumer/" + CONSUMER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Failed to delete consumer: the consumer does not exist\"}", true));

        verify(consumerManager).removeConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void receiveMessage_noMessage_returnsNull() throws Exception {
        when(consumerManager.findConsumer(any())).thenReturn(Optional.of(singleConsumerService));
        when(singleConsumerService.receive()).thenReturn(Optional.empty());

        mockMvc.perform(post("/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"body\": null, \"properties\": [], headers: null}", true));

        verify(consumerManager).findConsumer(CONSUMER_ID);
        verify(singleConsumerService).receive();
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void receiveMessage_message_returnsMessage() throws Exception {
        when(consumerManager.findConsumer(any())).thenReturn(Optional.of(singleConsumerService));
        when(singleConsumerService.receive()).thenReturn(Optional.of(MESSAGE));
        when(messageModelFactory.create(any())).thenReturn(new MessageModel(new HeadersModel(null, null), emptyList(), new TextBodyModel(TEXT)));

        mockMvc.perform(post("/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"body\": {\"type\": \"text\", \"text\": \"" + TEXT + "\"}, \"properties\": []}"));

        verify(consumerManager).findConsumer(CONSUMER_ID);
        verify(singleConsumerService).receive();
        verify(messageModelFactory).create(MESSAGE);
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void receiveMessage_consumerDoesNotExist_returnsBadRequest() throws Exception {
        when(consumerManager.findConsumer(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/consumer/" + CONSUMER_ID + "/receive"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Failed to receive message: the consumer does not exist\"}"));

        verify(consumerManager).findConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void acknowledge_callsConsumerAcknowledge() throws Exception {
        when(consumerManager.findConsumer(any())).thenReturn(Optional.of(singleConsumerService));

        mockMvc.perform(post("/consumer/" + CONSUMER_ID + "/acknowledge")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"" + MESSAGE_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(consumerManager).findConsumer(CONSUMER_ID);
        verify(singleConsumerService).acknowledge(MESSAGE_ID);
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void acknowledge_consumerDoesNotExist_returnsBadRequest() throws Exception {
        when(consumerManager.findConsumer(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/consumer/" + CONSUMER_ID + "/acknowledge")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"" + MESSAGE_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Failed to acknowledge message: the consumer does not exist\"}"));

        verify(consumerManager).findConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void recover_callsConsumerRecover() throws Exception {
        when(consumerManager.findConsumer(any())).thenReturn(Optional.of(singleConsumerService));

        mockMvc.perform(post("/consumer/" + CONSUMER_ID + "/recover"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(consumerManager).findConsumer(CONSUMER_ID);
        verify(singleConsumerService).recover();
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }

    @Test
    public void recover_consumerDoesNotExist_returnsBadRequest() throws Exception {
        when(consumerManager.findConsumer(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/consumer/" + CONSUMER_ID + "/recover"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\": \"Failed to recover consumer: the consumer does not exist\"}"));

        verify(consumerManager).findConsumer(CONSUMER_ID);
        verifyNoMoreInteractions(consumerManager, singleConsumerService, messageModelFactory);
    }
}
