package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;

public class MessageFactory {

    private final PropertiesFactory propertiesFactory;

    public MessageFactory(PropertiesFactory propertiesFactory) {
        this.propertiesFactory = propertiesFactory;
    }

    public Message create(Destination destination, MessageModel messageModel) throws MessageFormatException {
        if (messageModel.getMessage() == null) {
            return null;
        }
        return new Message(destination, propertiesFactory.create(messageModel.getProperties()), messageModel.getMessage());
    }
}
