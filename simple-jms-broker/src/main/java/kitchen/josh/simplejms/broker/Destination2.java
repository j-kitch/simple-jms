package kitchen.josh.simplejms.broker;

import java.util.UUID;

public class Destination2 {

    private final Destination type;
    private final UUID id;

    public Destination2(Destination type, UUID id) {
        this.type = type;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Destination getType() {
        return type;
    }
}
