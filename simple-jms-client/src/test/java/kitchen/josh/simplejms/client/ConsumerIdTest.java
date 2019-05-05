package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ConsumerIdTest {

    @Test
    public void newConsumerId_nullDestination_throwsIllegalArgument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ConsumerId(null, UUID.randomUUID()))
                .withMessage("Destination is required");
    }

    @Test
    public void newConsumerId_nullUUID_throwsIllegalArgument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ConsumerId(new Destination(DestinationType.QUEUE, UUID.randomUUID()), null))
                .withMessage("UUID is required");
    }
}