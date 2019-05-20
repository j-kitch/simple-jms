package kitchen.josh.simplejms.common.message;

import kitchen.josh.simplejms.common.message.body.Body;
import kitchen.josh.simplejms.common.message.body.BodyFactory;
import kitchen.josh.simplejms.common.message.body.ObjectBody;
import kitchen.josh.simplejms.common.message.body.TextBody;
import kitchen.josh.simplejms.common.message.headers.Headers;
import kitchen.josh.simplejms.common.message.headers.HeadersFactory;
import kitchen.josh.simplejms.common.message.properties.Properties;
import kitchen.josh.simplejms.common.message.properties.PropertiesFactory;

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
            return new TextMessage(headers, properties, (TextBody) body);
        }
        if (body.getClass() == ObjectBody.class) {
            return new ObjectMessage(headers, properties, (ObjectBody) body);
        }
        throw new RuntimeException("");
    }
}
