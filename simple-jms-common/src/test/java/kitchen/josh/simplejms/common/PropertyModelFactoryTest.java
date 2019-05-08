package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyModelFactoryTest {

    private PropertyModelFactory propertyModelFactory;

    @Before
    public void setUp() {
        propertyModelFactory = new PropertyModelFactory();
    }

    @Test
    public void createPropertyModels_noProperties_returnsEmptyList() {
        Properties properties = new Properties();

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).isEmpty();
    }

    @Test
    public void createPropertyModels_booleanProperty_returnsBooleanModel() {
        Properties properties = new Properties();
        properties.setBooleanProperty("property", true);

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactly(new PropertyModel("property", "Boolean", true));
    }

    @Test
    public void createPropertyModels_byteProperty_returnsByteModel() {
        Properties properties = new Properties();
        properties.setByteProperty("property", (byte) 2);

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactly(new PropertyModel("property", "Byte", (byte) 2));
    }

    @Test
    public void createPropertyModels_shortProperty_returnsShortModel() {
        Properties properties = new Properties();
        properties.setShortProperty("property", (short) 3);

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactly(new PropertyModel("property", "Short", (short) 3));
    }

    @Test
    public void createPropertyModels_intProperty_returnsIntModel() {
        Properties properties = new Properties();
        properties.setIntProperty("property", 4);

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactly(new PropertyModel("property", "Integer", 4));
    }

    @Test
    public void createPropertyModels_longProperty_returnsLongModel() {
        Properties properties = new Properties();
        properties.setLongProperty("property", 5);

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactly(new PropertyModel("property", "Long", 5L));
    }

    @Test
    public void createPropertyModels_floatProperty_returnsFloatModel() {
        Properties properties = new Properties();
        properties.setFloatProperty("property", 1.2f);

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactly(new PropertyModel("property", "Float", 1.2f));
    }

    @Test
    public void createPropertyModels_doubleProperty_returnsDoubleModel() {
        Properties properties = new Properties();
        properties.setDoubleProperty("property", 2.3);

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactly(new PropertyModel("property", "Double", 2.3));
    }

    @Test
    public void createPropertyModels_stringProperty_returnsStringModel() {
        Properties properties = new Properties();
        properties.setStringProperty("property", "hello world");

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactly(new PropertyModel("property", "String", "hello world"));
    }

    @Test
    public void createPropertyModels_multipleProperties_returnsMultipleModels() {
        Properties properties = new Properties();
        properties.setFloatProperty("property 1", 1.2f);
        properties.setDoubleProperty("property 2", 2.3);
        properties.setByteProperty("property 3", (byte) 2);

        List<PropertyModel> propertyModels = propertyModelFactory.createPropertyModels(properties);

        assertThat(propertyModels).containsExactlyInAnyOrder(
                new PropertyModel("property 1", "Float", 1.2f),
                new PropertyModel("property 2", "Double", 2.3),
                new PropertyModel("property 3", "Byte", (byte) 2));
    }
}