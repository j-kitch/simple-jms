package kitchen.josh.simplejms.broker;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.UUID;

public class Destination {

    private final DestinationType type;
    private final UUID id;

    public Destination(DestinationType type, UUID id) {
        Assert.notNull(type, "DestinationType is required");
        Assert.notNull(id, "UUID is required");
        this.type = type;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public DestinationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
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
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
