package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class MessageModel {

    private final List<PropertyModel> properties;
    private final BodyModel body;

    public MessageModel(@JsonProperty("properties") List<PropertyModel> properties, @JsonProperty("body") BodyModel body) {
        this.properties = properties;
        this.body = body;
    }

    public List<PropertyModel> getProperties() {
        return properties;
    }

    public BodyModel getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageModel that = (MessageModel) o;
        return Objects.equals(properties, that.properties) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, body);
    }
}
