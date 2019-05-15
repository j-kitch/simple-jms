package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.MessageFormatException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageFactoryTest {

    private static final Destination DESTINATION = new Destination(DestinationType.TOPIC, UUID.randomUUID());
    private static final String MESSAGE = "hello world";
    private static final Properties PROPERTIES = createProperties();
    private static final List<PropertyModel> PROPERTY_MODELS = createPropertyModels();

    @Mock
    private PropertiesFactory propertiesFactory;

    private MessageFactory messageFactory;

    @Before
    public void setUp() {
        messageFactory = new MessageFactory(propertiesFactory);
    }

    @Test
    public void create_messageTextIsNull_returnsNull() throws MessageFormatException {
        MessageModel messageModel = new MessageModel(Collections.emptyList(), null);

        Message message = messageFactory.create(DESTINATION, messageModel);

        assertThat(message).isNull();
        verifyZeroInteractions(propertiesFactory);
    }

    @Test
    public void create_messageText_returnsMessage() throws MessageFormatException {
        MessageModel messageModel = new MessageModel(PROPERTY_MODELS, MESSAGE);
        when(propertiesFactory.create(any())).thenReturn(PROPERTIES);

        Message message = messageFactory.create(DESTINATION, messageModel);

        assertThat(message.getDestination()).isEqualTo(DESTINATION);
        assertThat(message.getBody()).isEqualTo(MESSAGE);
        assertThat(message.getProperties()).isEqualToComparingFieldByField(PROPERTIES);
        verify(propertiesFactory).create(PROPERTY_MODELS);
    }

    private static Properties createProperties() {
        Properties properties = new Properties();
        properties.setFloatProperty("property 1", 1.2f);
        properties.setBooleanProperty("property 2", false);
        return properties;
    }

    private static List<PropertyModel> createPropertyModels() {
        List<PropertyModel> propertyModels = new ArrayList<>();
        propertyModels.add(new PropertyModel("property 1", "Float", 1.2f));
        propertyModels.add(new PropertyModel("property 2", "Boolean", false));
        return propertyModels;
    }
}