package kitchen.josh.simplejms.client;

import kitchen.josh.simplejms.broker.Destination;

import java.util.UUID;

public class ConsumerId {

    private final Destination destination;
    private final UUID id;

    public ConsumerId(Destination destination, UUID id) {
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
