package kitchen.josh.simplejms.common;

public class MessageModelFactory {

    private final PropertyModelFactory propertyModelFactory;
    private final BodyModelFactory bodyModelFactory;

    public MessageModelFactory(PropertyModelFactory propertyModelFactory, BodyModelFactory bodyModelFactory) {
        this.propertyModelFactory = propertyModelFactory;
        this.bodyModelFactory = bodyModelFactory;
    }

    public MessageModel create(Message message) {
        return new MessageModel(
                propertyModelFactory.create(message),
                bodyModelFactory.create(message.getBody()));
    }
}
