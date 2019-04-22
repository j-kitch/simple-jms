package kitchen.josh.simplejms.broker;

public class Message {

    private final Destination destination;
    private final String message;

    public Message(Destination destination, String message) {
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
