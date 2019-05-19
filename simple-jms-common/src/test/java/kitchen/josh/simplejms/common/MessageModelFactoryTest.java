package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageModelFactoryTest {

    private static final String TEXT = "hello world";
    private static final HeadersModel HEADERS_MODEL = new HeadersModel(null, null);
    private static final List<PropertyModel> PROPERTY_MODELS = Collections.singletonList(new PropertyModel("property", "Integer", 0));
    private static final TextBodyModel TEXT_BODY_MODEL = new TextBodyModel(TEXT);

    @Mock
    private HeadersModelFactory headersModelFactory;

    @Mock
    private PropertyModelFactory propertyModelFactory;

    @Mock
    private BodyModelFactory bodyModelFactory;

    private MessageModelFactory messageModelFactory;

    @Before
    public void setUp() {
        messageModelFactory = new MessageModelFactory(headersModelFactory, propertyModelFactory, bodyModelFactory);
    }

    @Test
    public void create_delegatesToPropertiesAndBodyModelFactories() {
        TextMessage message = new TextMessage(new PropertiesImpl(), new TextBody(TEXT));
        message.setIntProperty("property", 0);

        when(headersModelFactory.create(any())).thenReturn(HEADERS_MODEL);
        when(propertyModelFactory.create(any())).thenReturn(PROPERTY_MODELS);
        when(bodyModelFactory.create(any())).thenReturn(TEXT_BODY_MODEL);

        MessageModel messageModel = messageModelFactory.create(message);

        assertThat(messageModel).isEqualToComparingFieldByFieldRecursively(
                new MessageModel(HEADERS_MODEL, PROPERTY_MODELS, TEXT_BODY_MODEL));
        verify(headersModelFactory).create(message);
        verify(propertyModelFactory).create(message);
        verify(bodyModelFactory).create(message.getBody());
    }
}