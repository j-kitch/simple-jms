package kitchen.josh.simplejms.common.message;

import kitchen.josh.simplejms.common.message.body.BodyModelFactory;
import kitchen.josh.simplejms.common.message.headers.HeadersModelFactory;
import kitchen.josh.simplejms.common.message.properties.PropertyModelFactory;

public class MessageModelFactory {

    private final HeadersModelFactory headersModelFactory;
    private final PropertyModelFactory propertyModelFactory;
    private final BodyModelFactory bodyModelFactory;

    public MessageModelFactory(HeadersModelFactory headersModelFactory,
                               PropertyModelFactory propertyModelFactory,
                               BodyModelFactory bodyModelFactory) {
        this.headersModelFactory = headersModelFactory;
        this.propertyModelFactory = propertyModelFactory;
        this.bodyModelFactory = bodyModelFactory;
    }

    public MessageModel create(Message message) {
        return new MessageModel(
                headersModelFactory.create(message),
                propertyModelFactory.create(message),
                bodyModelFactory.create(message.getBody()));
    }
}
