package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;

import java.util.UUID;

public class ProducerId {

    private final Destination destination;
    private final UUID id;

    public ProducerId(Destination destination, UUID id) {
        this.destination = destination;
        this.id = id;
    }

    public Destination getDestination() {
        return destination;
    }

    public UUID getId() {
        return id;
    }
}
