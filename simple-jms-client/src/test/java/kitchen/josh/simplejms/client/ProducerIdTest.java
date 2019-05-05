package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;
import kitchen.josh.simplejms.broker.DestinationType;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ProducerIdTest {

    @Test
    public void newProducerId_nullDestination_throwsIllegalArgument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ProducerId(null, UUID.randomUUID()))
                .withMessage("Destination is required");
    }

    @Test
    public void newProducerId_nullUUID_throwsIllegalArgument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ProducerId(new Destination(DestinationType.QUEUE, UUID.randomUUID()), null))
                .withMessage("UUID is required");
    }
}