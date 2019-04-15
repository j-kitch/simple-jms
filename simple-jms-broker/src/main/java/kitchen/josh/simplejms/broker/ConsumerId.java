package kitchen.josh.simplejms.broker;

import java.util.UUID;

class ConsumerId {

    private final UUID id;

    ConsumerId(UUID id) {
        this.id = id;
    }

    UUID getId() {
        return id;
    }
}
