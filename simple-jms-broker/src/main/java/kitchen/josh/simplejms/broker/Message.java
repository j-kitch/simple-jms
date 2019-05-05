package kitchen.josh.simplejms.broker;

import org.springframework.util.Assert;

public class Message {

    private final Destination destination;
    private final String message;

    public Message(Destination destination, String message) {
        Assert.notNull(destination, "Destination is required");
        Assert.notNull(message, "String is required");
        this.destination = destination;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Destination getDestination() {
        return destination;
    }
}
