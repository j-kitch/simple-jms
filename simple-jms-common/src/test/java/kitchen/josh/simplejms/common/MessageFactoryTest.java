package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.MessageFormatException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageFactoryTest {

    private static final String TEXT = "hello world";
    private static final Headers HEADERS = new HeadersImpl();
    private static final HeadersModel HEADERS_MODEL = new HeadersModel(null, null);
    private static final Properties PROPERTIES = createProperties();
    private static final List<PropertyModel> PROPERTY_MODELS = createPropertyModels();
    private static final byte[] BYTES = {1, 2, 3, 4};
    private static final Serializable OBJECT = 2;
    private static final ObjectBodyModel OBJECT_BODY_MODEL = new ObjectBodyModel(BYTES);
    private static final TextBodyModel TEXT_BODY_MODEL = new TextBodyModel(TEXT);

    @Mock
    private HeadersFactory headersFactory;

    @Mock
    private PropertiesFactory propertiesFactory;

    @Mock
    private BodyFactory bodyFactory;

    private MessageFactory messageFactory;

    @Before
    public void setUp() {
        messageFactory = new MessageFactory(headersFactory, propertiesFactory, bodyFactory);
    }

    @Test
    public void create_messageBodyIsNull_returnsNull() throws MessageFormatException {
        MessageModel messageModel = new MessageModel(HEADERS_MODEL, emptyList(), null);

        Message message = messageFactory.create(messageModel);

        assertThat(message).isNull();
        verifyZeroInteractions(propertiesFactory);
    }

    @Test
    public void create_textMessage_returnsTextMessage() throws MessageFormatException {
        MessageModel messageModel = new MessageModel(HEADERS_MODEL, PROPERTY_MODELS, TEXT_BODY_MODEL);
        when(headersFactory.create(any())).thenReturn(HEADERS);
        when(propertiesFactory.create(any())).thenReturn(PROPERTIES);
        when(bodyFactory.create(any())).thenReturn(new TextBody(TEXT));

        Message message = messageFactory.create(messageModel);

        assertThat(message).isEqualToComparingFieldByFieldRecursively(new TextMessage(PROPERTIES, new TextBody(TEXT)));
        assertThat(message.getHeaders()).isEqualToComparingFieldByField(HEADERS);
        verify(headersFactory).create(HEADERS_MODEL);
        verify(propertiesFactory).create(PROPERTY_MODELS);
        verify(bodyFactory).create(TEXT_BODY_MODEL);
    }

    @Test
    public void create_objectMessage_returnsObjectMessage() throws MessageFormatException {
        MessageModel messageModel = new MessageModel(HEADERS_MODEL, PROPERTY_MODELS, OBJECT_BODY_MODEL);
        when(headersFactory.create(any())).thenReturn(HEADERS);
        when(propertiesFactory.create(any())).thenReturn(PROPERTIES);
        when(bodyFactory.create(any())).thenReturn(new ObjectBody(OBJECT));

        Message message = messageFactory.create(messageModel);

        assertThat(message).isEqualToComparingFieldByFieldRecursively(new ObjectMessage(PROPERTIES, new ObjectBody(OBJECT)));
        assertThat(message.getHeaders()).isEqualToComparingFieldByField(HEADERS);
        verify(headersFactory).create(HEADERS_MODEL);
        verify(propertiesFactory).create(PROPERTY_MODELS);
        verify(bodyFactory).create(OBJECT_BODY_MODEL);
    }

    private static Properties createProperties() {
        Properties properties = new PropertiesImpl();
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