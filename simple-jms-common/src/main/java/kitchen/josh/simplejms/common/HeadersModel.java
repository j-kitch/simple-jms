package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeadersModel model = (HeadersModel) o;
        return Objects.equals(id, model.id) &&
                Objects.equals(destination, model.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, destination);
    }
}
