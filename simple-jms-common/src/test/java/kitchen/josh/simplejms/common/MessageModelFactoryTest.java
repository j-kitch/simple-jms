package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageModelFactoryTest {

    private static final Destination DESTINATION = new Destination(DestinationType.TOPIC, UUID.randomUUID());
    private static final String MESSAGE = "hello world";

    @Mock
    private PropertyModelFactory propertyModelFactory;

    private MessageModelFactory messageModelFactory;

    @Before
    public void setUp() {
        messageModelFactory = new MessageModelFactory(propertyModelFactory);
    }

    @Test
    public void create_copiesTextMessageAndDelegatesPropertiesToFactory() {
        TextMessage message = new TextMessage(new PropertiesImpl(), new TextBody());
        message.setText(MESSAGE);
        message.setIntProperty("property", 0);
        List<PropertyModel> propertyModels = Collections.singletonList(new PropertyModel("property", "Integer", 0));
        when(propertyModelFactory.create(any())).thenReturn(propertyModels);

        MessageModel messageModel = messageModelFactory.create(message);

        assertThat(messageModel).isEqualToComparingFieldByFieldRecursively(new MessageModel(propertyModels, new TextBodyModel(MESSAGE)));
        verify(propertyModelFactory).create(message);
    }
}