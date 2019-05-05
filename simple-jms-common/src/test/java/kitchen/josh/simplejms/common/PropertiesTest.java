package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.Collections.list;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PropertiesTest {

    private static final String PROPERTY_1 = "hello";
    private static final String PROPERTY_2 = "world";

    private Properties properties;

    @Before
    public void setUp() {
        properties = new Properties();
    }

    @Test
    public void clearProperties_noProperties_doesNothing() {
        properties.clearProperties();

        assertThat(properties.getObjectProperty(PROPERTY_1)).isNull();
        assertThat(properties.getObjectProperty(PROPERTY_2)).isNull();
    }

    @Test
    public void clearProperties_propertiesExist_removesProperties() {
        properties.setBooleanProperty(PROPERTY_1, true);
        properties.setDoubleProperty(PROPERTY_2, 2.3);

        properties.clearProperties();

        assertThat(properties.getObjectProperty(PROPERTY_1)).isNull();
        assertThat(properties.getObjectProperty(PROPERTY_2)).isNull();
    }

    @Test
    public void booleanProperty_conversions() throws JMSException {
        properties.setBooleanProperty(PROPERTY_1, false);

        assertThat(properties.getBooleanProperty(PROPERTY_1)).isFalse();
        assertThat(properties.getStringProperty(PROPERTY_1)).isEqualTo("false");
        assertThat(properties.getObjectProperty(PROPERTY_1)).isEqualTo(false);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getByteProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getShortProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getIntProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getLongProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getFloatProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getDoubleProperty(PROPERTY_1));
    }

    @Test
    public void byteProperty_conversions() throws JMSException {
        properties.setByteProperty(PROPERTY_1, (byte) 3);

        assertThat(properties.getByteProperty(PROPERTY_1)).isEqualTo((byte) 3);
        assertThat(properties.getShortProperty(PROPERTY_1)).isEqualTo((short) 3);
        assertThat(properties.getIntProperty(PROPERTY_1)).isEqualTo(3);
        assertThat(properties.getLongProperty(PROPERTY_1)).isEqualTo((long) 3);
        assertThat(properties.getStringProperty(PROPERTY_1)).isEqualTo("3");
        assertThat(properties.getObjectProperty(PROPERTY_1)).isEqualTo((byte) 3);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getBooleanProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getFloatProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getDoubleProperty(PROPERTY_1));
    }

    @Test
    public void shortProperty_conversions() throws JMSException {
        properties.setShortProperty(PROPERTY_1, (short) 3);

        assertThat(properties.getShortProperty(PROPERTY_1)).isEqualTo((short) 3);
        assertThat(properties.getIntProperty(PROPERTY_1)).isEqualTo((int) 3);
        assertThat(properties.getLongProperty(PROPERTY_1)).isEqualTo((long) 3);
        assertThat(properties.getStringProperty(PROPERTY_1)).isEqualTo("3");
        assertThat(properties.getObjectProperty(PROPERTY_1)).isEqualTo((short) 3);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getBooleanProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getByteProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getFloatProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getDoubleProperty(PROPERTY_1));
    }

    @Test
    public void intProperty_conversions() throws JMSException {
        properties.setIntProperty(PROPERTY_1, 4);

        assertThat(properties.getIntProperty(PROPERTY_1)).isEqualTo(4);
        assertThat(properties.getLongProperty(PROPERTY_1)).isEqualTo((long) 4);
        assertThat(properties.getStringProperty(PROPERTY_1)).isEqualTo("4");
        assertThat(properties.getObjectProperty(PROPERTY_1)).isEqualTo(4);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getBooleanProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getByteProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getShortProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getFloatProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getDoubleProperty(PROPERTY_1));
    }

    @Test
    public void longProperty_conversions() throws JMSException {
        properties.setLongProperty(PROPERTY_1, (long) 5);

        assertThat(properties.getLongProperty(PROPERTY_1)).isEqualTo((long) 5);
        assertThat(properties.getStringProperty(PROPERTY_1)).isEqualTo("5");
        assertThat(properties.getObjectProperty(PROPERTY_1)).isEqualTo((long) 5);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getBooleanProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getByteProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getShortProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getIntProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getFloatProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getDoubleProperty(PROPERTY_1));
    }

    @Test
    public void floatProperty_conversions() throws JMSException {
        properties.setFloatProperty(PROPERTY_1, (float) 1.2);

        assertThat(properties.getFloatProperty(PROPERTY_1)).isEqualTo((float) 1.2);
        assertThat(properties.getDoubleProperty(PROPERTY_1)).isEqualTo((double) (float) 1.2);
        assertThat(properties.getStringProperty(PROPERTY_1)).isEqualTo("1.2");
        assertThat(properties.getObjectProperty(PROPERTY_1)).isEqualTo((float) 1.2);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getBooleanProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getByteProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getShortProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getIntProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getLongProperty(PROPERTY_1));
    }

    @Test
    public void doubleProperty_conversions() throws JMSException {
        properties.setDoubleProperty(PROPERTY_1, 2.3);

        assertThat(properties.getDoubleProperty(PROPERTY_1)).isEqualTo(2.3);
        assertThat(properties.getStringProperty(PROPERTY_1)).isEqualTo("2.3");
        assertThat(properties.getObjectProperty(PROPERTY_1)).isEqualTo(2.3);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getBooleanProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getByteProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getShortProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getIntProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getLongProperty(PROPERTY_1));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.getFloatProperty(PROPERTY_1));
    }

    @Test
    public void stringProperty_nonNumeric_conversions() throws JMSException {
        properties.setStringProperty(PROPERTY_1, "hello world");

        assertThat(properties.getBooleanProperty(PROPERTY_1)).isFalse();

        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getByteProperty(PROPERTY_1));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getShortProperty(PROPERTY_1));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getIntProperty(PROPERTY_1));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getLongProperty(PROPERTY_1));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getFloatProperty(PROPERTY_1));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getDoubleProperty(PROPERTY_1));
    }

    @Test
    public void stringProperty_numeric_conversions() throws JMSException {
        properties.setStringProperty(PROPERTY_1, "10");

        assertThat(properties.getBooleanProperty(PROPERTY_1)).isFalse();
        assertThat(properties.getByteProperty(PROPERTY_1)).isEqualTo((byte) 10);
        assertThat(properties.getShortProperty(PROPERTY_1)).isEqualTo((short) 10);
        assertThat(properties.getIntProperty(PROPERTY_1)).isEqualTo(10);
        assertThat(properties.getLongProperty(PROPERTY_1)).isEqualTo((long) 10);
        assertThat(properties.getFloatProperty(PROPERTY_1)).isEqualTo((float) 10);
        assertThat(properties.getDoubleProperty(PROPERTY_1)).isEqualTo((double) 10);
    }

    @Test
    public void nullProperty_conversions() throws JMSException {
        assertThat(properties.getBooleanProperty(PROPERTY_1)).isFalse();
        assertThat(properties.getStringProperty(PROPERTY_1)).isNull();
        assertThat(properties.getObjectProperty(PROPERTY_1)).isNull();

        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getByteProperty(PROPERTY_1));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getShortProperty(PROPERTY_1));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getIntProperty(PROPERTY_1));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> properties.getLongProperty(PROPERTY_1));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> properties.getFloatProperty(PROPERTY_1));
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> properties.getDoubleProperty(PROPERTY_1));
    }

    @Test
    public void setObjectProperty() {
        Stream.of(false, (byte) 1, (short) 2, 3, (long) 4, (float) 5, (double) 6, "hello world").forEach(value -> {
            try {
                properties.setObjectProperty(PROPERTY_1, value);
            } catch (MessageFormatException e) {
                throw new RuntimeException(e);
            }
            assertThat(properties.getObjectProperty(PROPERTY_1)).isEqualTo(value);
        });

        Stream.of(new ArrayList<Integer>(), emptyMap(), new java.util.Properties(), 'c').forEach(value -> {
            assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> properties.setObjectProperty(PROPERTY_1, value));
        });
    }

    @Test
    public void propertyNames() {
        properties.setByteProperty(PROPERTY_1, (byte) 3);
        properties.setStringProperty(PROPERTY_2, "hello world");

        assertThat(list(properties.getPropertyNames())).containsExactlyInAnyOrder(PROPERTY_1, PROPERTY_2);
    }

    @Test
    public void propertyExists() {
        properties.setDoubleProperty(PROPERTY_1, 2.3);

        assertThat(properties.propertyExists(PROPERTY_1)).isTrue();
        assertThat(properties.propertyExists(PROPERTY_2)).isFalse();
    }
}