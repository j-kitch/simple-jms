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
    public void create_copiesMessageAndDelegatesPropertiesToFactory() {
        Message message = new Message(DESTINATION, MESSAGE);
        message.getProperties().setIntProperty("property", 0);
        Properties properties = message.getProperties();
        List<PropertyModel> propertyModels = Collections.singletonList(new PropertyModel("property", "Integer", 0));
        when(propertyModelFactory.create(any())).thenReturn(propertyModels);

        MessageModel messageModel = messageModelFactory.create(message);

        assertThat(messageModel).isEqualToComparingFieldByField(new MessageModel(propertyModels, MESSAGE));
        verify(propertyModelFactory).create(properties);
    }
}