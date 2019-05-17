package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;

public class MessageFactory {

    private final PropertiesFactory propertiesFactory;

    public MessageFactory(PropertiesFactory propertiesFactory) {
        this.propertiesFactory = propertiesFactory;
    }

    public TextMessage createTextMessage(MessageModel messageModel) throws MessageFormatException {
        if (messageModel.getBody() == null) {
            return null;
        }
        TextBody textBody = new TextBody();
        textBody.setText(messageModel.getBody().getText());
        return new TextMessage(propertiesFactory.create(messageModel.getProperties()), textBody);
    }
}
