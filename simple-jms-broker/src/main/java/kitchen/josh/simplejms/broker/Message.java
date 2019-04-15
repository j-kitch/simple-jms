package kitchen.josh.simplejms.broker;

import com.fasterxml.jackson.annotation.JsonProperty;

class Message {

    @JsonProperty
    private final String message;

    Message(String message) {
        this.message = message;
    }

    String getMessage() {
        return message;
    }
}
