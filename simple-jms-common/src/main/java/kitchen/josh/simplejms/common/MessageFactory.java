package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;

public class MessageFactory {

    private final PropertiesFactory propertiesFactory;
    private final BodyFactory bodyFactory;

    public MessageFactory(PropertiesFactory propertiesFactory, BodyFactory bodyFactory) {
        this.propertiesFactory = propertiesFactory;
        this.bodyFactory = bodyFactory;
    }

    public Message create(MessageModel messageModel) throws MessageFormatException {
        if (messageModel.getBody() == null) {
            return null;
        }
        Properties properties = propertiesFactory.create(messageModel.getProperties());
        Body body = bodyFactory.create(messageModel.getBody());
        if (body.getClass() == TextBody.class) {
            return new TextMessage(properties, (TextBody) body);
        }
        if (body.getClass() == ObjectBody.class) {
            return new ObjectMessage(properties, (ObjectBody) body);
        }
        throw new RuntimeException("");
    }
}
