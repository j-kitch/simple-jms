package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HeadersModel {

    private final String id;
    private final String destination;

    public HeadersModel(@JsonProperty("JMSMessageID") String id,
                        @JsonProperty("JMSDestination") String destination) {
        this.id = id;
        this.destination = destination;
    }

    @JsonGetter("JMSMessageID")
    public String getId() {
        return id;
    }

    @JsonGetter("JMSDestination")
    public String getDestination() {
        return destination;
    }
}
