package kitchen.josh.simplejms.common;

import java.util.List;

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
}
