package kitchen.josh.simplejms.broker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    @JsonProperty
    private final String message;

    @JsonCreator
    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
