package kitchen.josh.simplejms.common;

import org.springframework.util.Assert;

/**
 * A single message received by a destination.
 */
public final class Message {

    private final Destination destination;
    private final Properties properties;
    private final String body;

    public Message(Destination destination, String body) {
        Assert.notNull(destination, "Destination is required");
        Assert.notNull(body, "String is required");
        this.destination = destination;
        this.properties = new Properties();
        this.body = body;
    }

    public Message(Destination destination, Properties properties, String body) {
        this.destination = destination;
        this.properties = properties;
        this.body = body;
    }

    /**
     * Get the plain text body.
     *
     * @return the body
     */
    public final String getBody() {
        return body;
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
