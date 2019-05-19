package kitchen.josh.simplejms.common;

import java.util.List;
import java.util.Objects;

public class MessageModel {

    private List<PropertyModel> properties;
    private BodyModel body;

    public MessageModel() {

    }

    public MessageModel(List<PropertyModel> properties, BodyModel body) {
        this.properties = properties;
        this.body = body;
    }

    public List<PropertyModel> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyModel> properties) {
        this.properties = properties;
    }

    public BodyModel getBody() {
        return body;
    }

    public void setBody(BodyModel body) {
        this.body = body;
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
