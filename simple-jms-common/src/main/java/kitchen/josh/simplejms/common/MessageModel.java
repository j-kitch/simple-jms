package kitchen.josh.simplejms.common;

import java.util.List;
import java.util.Objects;

public class MessageModel {

    private List<PropertyModel> properties;
    private String message;

    public MessageModel() {

    }

    public MessageModel(List<PropertyModel> properties, String message) {
        this.properties = properties;
        this.message = message;
    }

    public List<PropertyModel> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyModel> properties) {
        this.properties = properties;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageModel that = (MessageModel) o;
        return Objects.equals(properties, that.properties) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, message);
    }
}
