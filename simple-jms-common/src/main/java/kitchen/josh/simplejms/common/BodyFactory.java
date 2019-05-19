package kitchen.josh.simplejms.common;

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
        TextBody textBody = new TextBody();
        textBody.setText(textBodyModel.getText());
        return textBody;
    }

    private ObjectBody createObjectBody(ObjectBodyModel objectBodyModel) {
        ObjectBody objectBody = new ObjectBody();
        objectBody.setObject((Serializable) deserialize(objectBodyModel.getBytes()));
        return objectBody;
    }
}
