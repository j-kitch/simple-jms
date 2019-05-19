package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorModel {

    private final String message;

    public ErrorModel(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
