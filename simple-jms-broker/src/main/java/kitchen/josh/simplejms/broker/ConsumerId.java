package kitchen.josh.simplejms.broker;

import java.util.UUID;

public class ConsumerId {

    private UUID id;

    public ConsumerId() {

    }

    public ConsumerId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
