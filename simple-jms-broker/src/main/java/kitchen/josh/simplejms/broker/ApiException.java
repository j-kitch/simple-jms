package kitchen.josh.simplejms.broker;

/**
 * A Broker's Rest API exception.
 */
public final class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
