package kitchen.josh.simplejms.broker;

import java.util.UUID;

public class IdModel {

    private UUID id;

    public IdModel() {

    }

    public IdModel(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
