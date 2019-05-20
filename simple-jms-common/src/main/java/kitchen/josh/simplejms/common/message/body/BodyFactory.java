package kitchen.josh.simplejms.common.message.body;

import java.io.Serializable;

import static org.springframework.util.SerializationUtils.deserialize;

public class BodyFactory {

    public Body create(BodyModel bodyModel) {
        if (bodyModel.getClass() == TextBodyModel.class) {
            return createTextBody((TextBodyModel) bodyModel);
        }
        if (bodyModel.getClass() == ObjectBodyModel.class) {
            return createObjectBody((ObjectBodyModel) bodyModel);
        }
        throw new RuntimeException("");
    }

    private TextBody createTextBody(TextBodyModel textBodyModel) {
        return new TextBody(textBodyModel.getText());

    }

    private ObjectBody createObjectBody(ObjectBodyModel objectBodyModel) {
        return new ObjectBody((Serializable) deserialize(objectBodyModel.getBytes()));
    }
}
