package kitchen.josh.simplejms.common;

import org.springframework.util.Assert;

/**
 * A single message received by a destination.
 */
public final class Message {

    private final Destination destination;
    private final Properties properties;
    private final String message;

    public Message(Destination destination, String message) {
        Assert.notNull(destination, "Destination is required");
        Assert.notNull(message, "String is required");
        this.destination = destination;
        this.properties = new Properties();
        this.message = message;
    }

    public Message(Destination destination, Properties properties, String message) {
        this.destination = destination;
        this.properties = properties;
        this.message = message;
    }

    /**
     * Get the plain text message.
     *
     * @return the message
     */
    public final String getMessage() {
        return message;
    }

    /**
     * Get the properties in this message.
     *
     * @return the message properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Get the destination the message was sent to.
     *
     * @return the destination the message was sent to
     */
    public final Destination getDestination() {
        return destination;
    }
}
