package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class IdModel {

    private final UUID id;

    public IdModel(@JsonProperty("id") UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
