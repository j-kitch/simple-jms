package kitchen.josh.simplejms.broker;

public class Message {

    private final Destination2 destination;
    private final String message;

    public Message(Destination2 destination, String message) {
        this.destination = destination;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Destination2 getDestination() {
        return destination;
    }
}
