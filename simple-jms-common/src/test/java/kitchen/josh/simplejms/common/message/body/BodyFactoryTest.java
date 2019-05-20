package kitchen.josh.simplejms.common.message.body;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.SerializationUtils.serialize;

public class BodyFactoryTest {

    private static final String TEXT = "hello world";
    private static final Serializable OBJECT = 2;
    private static final byte[] SERIALIZED_OBJECT = serialize(OBJECT);

    private BodyFactory bodyFactory;

    @Before
    public void setUp() {
        bodyFactory = new BodyFactory();
    }

    @Test
    public void create_textBodyModel_returnsTextBody() {
        TextBodyModel textBodyModel = new TextBodyModel(TEXT);

        Body body = bodyFactory.create(textBodyModel);

        assertThat(body).isEqualToComparingFieldByField(new TextBody(TEXT));
    }

    @Test
    public void create_objectBodyModel_returnsObjectBody() {
        ObjectBodyModel objectBodyModel = new ObjectBodyModel(SERIALIZED_OBJECT);

        Body body = bodyFactory.create(objectBodyModel);

        assertThat(body).isEqualToComparingFieldByField(new ObjectBody(OBJECT));
    }
}