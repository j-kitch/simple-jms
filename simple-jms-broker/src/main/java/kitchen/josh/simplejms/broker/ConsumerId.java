package kitchen.josh.simplejms.broker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ConsumerId {

    @JsonProperty
    private final UUID id;

    @JsonCreator
    public ConsumerId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
