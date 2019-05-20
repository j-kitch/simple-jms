package kitchen.josh.simplejms.common.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import kitchen.josh.simplejms.common.message.body.BodyModel;
import kitchen.josh.simplejms.common.message.headers.HeadersModel;
import kitchen.josh.simplejms.common.message.properties.PropertyModel;

import java.util.List;
import java.util.Objects;

public class MessageModel {

    private final HeadersModel headers;
    private final List<PropertyModel> properties;
    private final BodyModel body;

    public MessageModel(@JsonProperty("headers") HeadersModel headers,
                        @JsonProperty("properties") List<PropertyModel> properties,
                        @JsonProperty("body") BodyModel body) {
        this.headers = headers;
        this.properties = properties;
        this.body = body;
    }

    public HeadersModel getHeaders() {
        return headers;
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
        return Objects.equals(headers, that.headers) &&
                Objects.equals(properties, that.properties) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, properties, body);
    }
}
