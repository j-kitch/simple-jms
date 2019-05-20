package kitchen.josh.simplejms.common.message.body;

public class BodyModelFactory {

    public BodyModel create(Body body) {
        if (body instanceof TextBody) {
            return createTextBodyModel((TextBody) body);
        }
        if (body instanceof ObjectBody) {
            return createObjectBodyModel((ObjectBody) body);
        }
        throw new RuntimeException("");
    }

    private static TextBodyModel createTextBodyModel(TextBody textBody) {
        return new TextBodyModel(textBody.getText());
    }

    private static ObjectBodyModel createObjectBodyModel(ObjectBody objectBody) {
        return new ObjectBodyModel(objectBody.getBytes());
    }
}
