package kitchen.josh.simplejms.common.message.body;

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
        BodyModel result = bodyModelFactory.create(new TextBody(TEXT));

        assertThat(result).isEqualToComparingFieldByField(new TextBodyModel(TEXT));
    }

    @Test
    public void create_objectBody_returnsObjectBodyModel() {
        BodyModel result = bodyModelFactory.create(new ObjectBody(OBJECT));

        assertThat(result).isEqualToComparingFieldByField(new ObjectBodyModel(ENCODED_OBJECT));
    }
}