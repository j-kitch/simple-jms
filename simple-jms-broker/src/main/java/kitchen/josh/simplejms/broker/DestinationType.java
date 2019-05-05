package kitchen.josh.simplejms.broker;

/**
 * The type of destination available.
 */
public enum DestinationType {

    /**
     * A publish/subscribe model of destination.
     */
    TOPIC,

    /**
     * A point-to-point model of destination.
     */
    QUEUE
}
