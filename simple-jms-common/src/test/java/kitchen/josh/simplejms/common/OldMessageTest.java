package kitchen.josh.simplejms.common;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OldMessageTest {

    @Test
    public void newMessage_nullDestination_throwsIllegalArgument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new OldMessage(null, ""))
                .withMessage("Destination is required");
    }

    @Test
    public void newMessage_nullString_throwsIllegalArgument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new OldMessage(new Destination(DestinationType.QUEUE, UUID.randomUUID()), null))
                .withMessage("String is required");
    }
}