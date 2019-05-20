package kitchen.josh.simplejms.common.message.headers;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HeadersFactoryTest {

    private static final String MESSAGE_ID = "ID:" + UUID.randomUUID();
    private static final Destination DESTINATION = new Destination(DestinationType.QUEUE, UUID.randomUUID());
    private static final String DESTINATION_STRING = "queue:" + DESTINATION.getId();

    private HeadersFactory headersFactory;

    @Before
    public void setUp() {
        headersFactory = new HeadersFactory();
    }

    @Test
    public void create() {
        Headers expected = new HeadersImpl();
        expected.setDestination(DESTINATION);
        expected.setId(MESSAGE_ID);

        Headers actual = headersFactory.create(new HeadersModel(MESSAGE_ID, DESTINATION_STRING));

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}