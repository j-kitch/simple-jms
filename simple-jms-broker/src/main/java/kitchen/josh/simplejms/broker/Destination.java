package kitchen.josh.simplejms.broker;

import java.util.UUID;

public class Destination {

    private final DestinationType type;
    private final UUID id;

    public Destination(DestinationType type, UUID id) {
        this.type = type;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public DestinationType getType() {
        return type;
    }
}
