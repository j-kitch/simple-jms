package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HeadersModelFactoryTest {

    private static final String MESSAGE_ID = "ID:" + UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, UUID.randomUUID());
    private static final String DESTINATION_STRING = "queue:" + DESTINATION.getId();

    private HeadersModelFactory headersModelFactory;

    @Before
    public void setUp() {
        headersModelFactory = new HeadersModelFactory();
    }

    @Test
    public void create() {
        Headers headers = new HeadersImpl();
        headers.setId(MESSAGE_ID);
        headers.setDestination(DESTINATION);

        HeadersModel model = headersModelFactory.create(headers);

        assertThat(model).isEqualToComparingFieldByField(new HeadersModel(MESSAGE_ID, DESTINATION_STRING));
    }
}