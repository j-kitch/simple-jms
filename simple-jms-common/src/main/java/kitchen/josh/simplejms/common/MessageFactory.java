package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;

public class MessageFactory {

    private final PropertiesFactory propertiesFactory;

    public MessageFactory(PropertiesFactory propertiesFactory) {
        this.propertiesFactory = propertiesFactory;
    }

    public OldMessage create(Destination destination, MessageModel messageModel) throws MessageFormatException {
        if (messageModel.getBody() == null) {
            return null;
        }
        TextBodyModel textBodyModel = messageModel.getBody();
        return new OldMessage(destination, propertiesFactory.create(messageModel.getProperties()), textBodyModel.getText());
    }
}
