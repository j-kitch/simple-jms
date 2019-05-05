package kitchen.josh.simplejms.broker;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.UUID;

/**
 * A unique, immutable description of a broker's destination.
 */
public final class Destination {

    private final DestinationType type;
    private final UUID id;

    /**
     * Create a new destination with the given type and id.
     *
     * @param type the type of destination
     * @param id   the id of the destination
     */
    public Destination(DestinationType type, UUID id) {
        Assert.notNull(type, "DestinationType is required");
        Assert.notNull(id, "UUID is required");
        this.type = type;
        this.id = id;
    }

    /**
     * Get the id of the destination.
     *
     * @return the id of the destination
     */
    public final UUID getId() {
        return id;
    }

    /**
     * Get the type of the destination.
     *
     * @return the type of the destination
     */
    public final DestinationType getType() {
        return type;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Destination that = (Destination) o;
        return type == that.type && id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(type, id);
    }
}
