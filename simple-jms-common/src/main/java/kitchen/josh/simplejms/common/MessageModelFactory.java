package kitchen.josh.simplejms.common;

public class MessageModelFactory {

    private final PropertyModelFactory propertyModelFactory;

    public MessageModelFactory(PropertyModelFactory propertyModelFactory) {
        this.propertyModelFactory = propertyModelFactory;
    }

    public MessageModel create(Message message) {
        return new MessageModel(
                propertyModelFactory.create(message.getProperties()),
                new TextBodyModel(message.getBody().getText()));
    }
}
