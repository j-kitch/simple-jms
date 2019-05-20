package kitchen.josh.simplejms.common.message.properties;

import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;

import static java.util.Arrays.asList;
import static java.util.Collections.list;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesFactoryTest {

    private static final String NAME = "property 1";
    private static final String NAME_2 = "property 2";

    private PropertiesFactory propertiesFactory;

    @Before
    public void setUp() {
        propertiesFactory = new PropertiesFactory();
    }

    @Test
    public void create_booleanModel_booleanProperty() throws JMSException {
        Properties properties = propertiesFactory.create(singletonList(new PropertyModel(NAME, "Boolean", true)));

        assertThat(properties.getBooleanProperty(NAME)).isTrue();
        assertThat(list(properties.getPropertyNames())).containsExactly(NAME);
    }

    @Test
    public void create_byteModel_byteProperty() throws JMSException {
        Properties properties = propertiesFactory.create(singletonList(new PropertyModel(NAME, "Byte", (byte) 2)));

        assertThat(properties.getByteProperty(NAME)).isEqualTo((byte) 2);
        assertThat(list(properties.getPropertyNames())).containsExactly(NAME);
    }

    @Test
    public void create_shortModel_shortProperty() throws JMSException {
        Properties properties = propertiesFactory.create(singletonList(new PropertyModel(NAME, "Short", (short) 3)));

        assertThat(properties.getShortProperty(NAME)).isEqualTo((short) 3);
        assertThat(list(properties.getPropertyNames())).containsExactly(NAME);
    }

    @Test
    public void create_intModel_intProperty() throws JMSException {
        Properties properties = propertiesFactory.create(singletonList(new PropertyModel(NAME, "Integer", 4)));

        assertThat(properties.getIntProperty(NAME)).isEqualTo(4);
        assertThat(list(properties.getPropertyNames())).containsExactly(NAME);
    }

    @Test
    public void create_longModel_longProperty() throws JMSException {
        Properties properties = propertiesFactory.create(singletonList(new PropertyModel(NAME, "Long", 5L)));

        assertThat(properties.getLongProperty(NAME)).isEqualTo(5L);
        assertThat(list(properties.getPropertyNames())).containsExactly(NAME);
    }

    @Test
    public void create_floatModel_floatProperty() throws JMSException {
        Properties properties = propertiesFactory.create(singletonList(new PropertyModel(NAME, "Float", 1.2f)));

        assertThat(properties.getFloatProperty(NAME)).isEqualTo(1.2f);
        assertThat(list(properties.getPropertyNames())).containsExactly(NAME);
    }

    @Test
    public void create_doubleModel_doubleProperty() throws JMSException {
        Properties properties = propertiesFactory.create(singletonList(new PropertyModel(NAME, "Double", 2.3)));

        assertThat(properties.getDoubleProperty(NAME)).isEqualTo(2.3);
        assertThat(list(properties.getPropertyNames())).containsExactly(NAME);
    }

    @Test
    public void create_stringModel_stringProperty() throws JMSException {
        Properties properties = propertiesFactory.create(singletonList(new PropertyModel(NAME, "String", "hello world")));

        assertThat(properties.getStringProperty(NAME)).isEqualTo("hello world");
        assertThat(list(properties.getPropertyNames())).containsExactly(NAME);
    }

    @Test
    public void create_multipleModels_multipleProperties() throws JMSException {
        Properties properties = propertiesFactory.create(asList(
                new PropertyModel(NAME, "Float", 1.2f),
                new PropertyModel(NAME_2, "String", "hello world")));

        assertThat(properties.getFloatProperty(NAME)).isEqualTo(1.2f);
        assertThat(properties.getStringProperty(NAME_2)).isEqualTo("hello world");
        assertThat(list(properties.getPropertyNames())).containsExactlyInAnyOrder(NAME, NAME_2);
    }
}