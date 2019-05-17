package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;
import java.util.List;

public class PropertiesFactory {

    public Properties create(List<PropertyModel> propertyModels) throws MessageFormatException {
        PropertiesImpl properties = new PropertiesImpl();
        for (PropertyModel model : propertyModels) {
            properties.setObjectProperty(model.getName(), model.getValue());
        }
        return properties;
    }
}
