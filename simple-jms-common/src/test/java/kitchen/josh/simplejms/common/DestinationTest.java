package kitchen.josh.simplejms.common;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DestinationTest {

    @Test
    public void newDestination_nullType_throwsIllegalArgument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Destination(null, UUID.randomUUID()))
                .withMessage("DestinationType is required");
    }

    @Test
    public void newDestination_nullUUID_throwsIllegalArgument() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Destination(DestinationType.QUEUE, null))
                .withMessage("UUID is required");
    }

    @Test
    public void equals_null_isFalse() {
        assertThat(new Destination(DestinationType.QUEUE, UUID.randomUUID()).equals(null)).isFalse();
    }

    @Test
    public void equals_self_isTrue() {
        Destination destination = new Destination(DestinationType.QUEUE, UUID.randomUUID());

        assertThat(destination.equals(destination)).isTrue();
    }

    @Test
    public void equals_otherType_isFalse() {
        Destination destination = new Destination(DestinationType.QUEUE, UUID.randomUUID());

        assertThat(destination.equals("hello world")).isFalse();
    }

    @Test
    public void equals_unequalType_isFalse() {
        UUID uuid = UUID.randomUUID();

        assertThat(new Destination(DestinationType.QUEUE, uuid).equals(new Destination(DestinationType.TOPIC, uuid))).isFalse();
        assertThat(new Destination(DestinationType.TOPIC, uuid).equals(new Destination(DestinationType.QUEUE, uuid))).isFalse();
    }

    @Test
    public void equals_unequalUUID_isFalse() {
        assertThat(new Destination(DestinationType.QUEUE, UUID.randomUUID()).equals(new Destination(DestinationType.QUEUE, UUID.randomUUID()))).isFalse();
    }

    @Test
    public void equals_equalTypeAndUUID_isTrue() {
        UUID uuid = UUID.randomUUID();

        assertThat(new Destination(DestinationType.QUEUE, uuid).equals(new Destination(DestinationType.QUEUE, uuid))).isTrue();
        assertThat(new Destination(DestinationType.TOPIC, uuid).equals(new Destination(DestinationType.TOPIC, uuid))).isTrue();
    }
}