package kitchen.josh.simplejms.common;

public class MessageModelFactory {

    private final PropertyModelFactory propertyModelFactory;

    public MessageModelFactory(PropertyModelFactory propertyModelFactory) {
        this.propertyModelFactory = propertyModelFactory;
    }

    public MessageModel create(TextMessage message) {
        return new MessageModel(
                propertyModelFactory.create(message),
                new TextBodyModel(message.getText()));
    }
}
