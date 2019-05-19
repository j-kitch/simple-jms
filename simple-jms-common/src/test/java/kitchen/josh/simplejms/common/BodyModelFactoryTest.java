package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.SerializationUtils.serialize;

public class BodyModelFactoryTest {

    private static final String TEXT = "hello world";
    private static final Serializable OBJECT = 2;
    private static final byte[] ENCODED_OBJECT = serialize(OBJECT);

    private BodyModelFactory bodyModelFactory;

    @Before
    public void setUp() {
        bodyModelFactory = new BodyModelFactory();
    }

    @Test
    public void create_textBody_returnsTextBodyModel() {
        TextBody textBody = new TextBody();
        textBody.setText(TEXT);

        BodyModel result = bodyModelFactory.create(textBody);

        assertThat(result).isEqualToComparingFieldByField(new TextBodyModel(TEXT));
    }

    @Test
    public void create_objectBody_returnsObjectBodyModel() {
        ObjectBody objectBody = new ObjectBody();
        objectBody.setObject(OBJECT);

        BodyModel result = bodyModelFactory.create(objectBody);

        assertThat(result).isEqualToComparingFieldByField(new ObjectBodyModel(ENCODED_OBJECT));
    }
}