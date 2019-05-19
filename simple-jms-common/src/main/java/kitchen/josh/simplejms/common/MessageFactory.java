package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;

public class MessageFactory {

    private final HeadersFactory headersFactory;
    private final PropertiesFactory propertiesFactory;
    private final BodyFactory bodyFactory;

    public MessageFactory(HeadersFactory headersFactory, PropertiesFactory propertiesFactory, BodyFactory bodyFactory) {
        this.headersFactory = headersFactory;
        this.propertiesFactory = propertiesFactory;
        this.bodyFactory = bodyFactory;
    }

    public Message create(MessageModel messageModel) throws MessageFormatException {
        if (messageModel.getBody() == null) {
            return null;
        }
        Headers headers = headersFactory.create(messageModel.getHeaders());
        Properties properties = propertiesFactory.create(messageModel.getProperties());
        Body body = bodyFactory.create(messageModel.getBody());
        if (body.getClass() == TextBody.class) {
            TextMessage textMessage = new TextMessage(properties, (TextBody) body);
            textMessage.setId(headers.getId());
            textMessage.setDestination(headers.getDestination());
            return textMessage;
        }
        if (body.getClass() == ObjectBody.class) {
            ObjectMessage objectMessage = new ObjectMessage(properties, (ObjectBody) body);
            objectMessage.setId(headers.getId());
            objectMessage.setDestination(headers.getDestination());
            return objectMessage;
        }
        throw new RuntimeException("");
    }
}
