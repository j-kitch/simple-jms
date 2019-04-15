package kitchen.josh.simplejms.broker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

class ConsumerId {

    @JsonProperty
    private final UUID id;

    @JsonCreator
    ConsumerId(UUID id) {
        this.id = id;
    }

    UUID getId() {
        return id;
    }
}
