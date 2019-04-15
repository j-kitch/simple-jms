package kitchen.josh.simplejms.broker;

import java.util.UUID;

public class ConsumerId {

    private final UUID id;

    public ConsumerId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
