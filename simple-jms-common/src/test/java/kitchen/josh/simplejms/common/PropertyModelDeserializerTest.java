package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PropertyModelDeserializerTest {

    private static final String NAME = "property name";

    // Values for each property type that cannot be narrowly cast (for numeric types).
    private static final boolean BOOLEAN = false;
    private static final byte BYTE = Byte.MAX_VALUE;
    private static final short SHORT = Short.MAX_VALUE;
    private static final int INT = Integer.MAX_VALUE;
    private static final long LONG = Long.MAX_VALUE;
    private static final float FLOAT = Float.MAX_VALUE;
    private static final double DOUBLE = Double.MAX_VALUE;
    private static final String STRING = "\"hello world\"";
    private static final String OBJECT = "{\"a\": 1}";
    private static final String ARRAY = "[1, 2, 3]";

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void booleanProperty() throws Exception {
        PropertyModel model = objectMapper.readValue(json(Boolean.class, BOOLEAN), PropertyModel.class);

        assertThat(model).isEqualToComparingFieldByField(new PropertyModel(NAME, "Boolean", BOOLEAN));
    }

    @Test
    public void booleanType_notBooleanValue_throwsJsonParse() {
        Object[] notBooleans = {BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, OBJECT, ARRAY};

        for (Object value : notBooleans) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json(Boolean.class, value), PropertyModel.class));
        }
    }

    @Test
    public void byteProperty() throws Exception {
        PropertyModel model = objectMapper.readValue(json(Byte.class, BYTE), PropertyModel.class);

        assertThat(model).isEqualToComparingFieldByField(new PropertyModel(NAME, "Byte", BYTE));
    }

    @Test
    public void byteType_notByteValue_throwsJsonParse() {
        for (Object value : new Object[]{BOOLEAN, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, OBJECT, ARRAY}) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json(Byte.class, value), PropertyModel.class));
        }
    }

    @Test
    public void shortProperty() throws Exception {
        PropertyModel model = objectMapper.readValue(json(Short.class, SHORT), PropertyModel.class);

        assertThat(model).isEqualToComparingFieldByField(new PropertyModel(NAME, "Short", SHORT));
    }

    @Test
    public void shortType_notShortValue_throwsJsonParse() {
        for (Object value : new Object[]{BOOLEAN, INT, LONG, FLOAT, DOUBLE, STRING, OBJECT, ARRAY}) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json(Short.class, value), PropertyModel.class));
        }
    }

    @Test
    public void intProperty() throws Exception {
        PropertyModel model = objectMapper.readValue(json(Integer.class, INT), PropertyModel.class);

        assertThat(model).isEqualToComparingFieldByField(new PropertyModel(NAME, "Integer", INT));
    }

    @Test
    public void intType_notIntValue_throwsJsonParse() {
        for (Object value : new Object[]{BOOLEAN, LONG, FLOAT, DOUBLE, STRING, OBJECT, ARRAY}) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json(Integer.class, value), PropertyModel.class));
        }
    }

    @Test
    public void longProperty() throws Exception {
        PropertyModel model = objectMapper.readValue(json(Long.class, LONG), PropertyModel.class);

        assertThat(model).isEqualToComparingFieldByField(new PropertyModel(NAME, "Long", LONG));
    }

    @Test
    public void longType_notLongValue_throwsJsonParse() {
        for (Object value : new Object[]{BOOLEAN, FLOAT, DOUBLE, STRING, OBJECT, ARRAY}) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json(Long.class, value), PropertyModel.class));
        }
    }

    @Test
    public void floatProperty() throws Exception {
        PropertyModel model = objectMapper.readValue(json(Float.class, FLOAT), PropertyModel.class);

        assertThat(model).isEqualToComparingFieldByField(new PropertyModel(NAME, "Float", FLOAT));
    }

    @Test
    public void floatType_notFloatValue_throwsJsonParse() {
        for (Object value : new Object[]{BOOLEAN, DOUBLE, STRING, OBJECT, ARRAY}) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json(Float.class, value), PropertyModel.class));
        }
    }

    @Test
    public void doubleProperty() throws Exception {
        PropertyModel model = objectMapper.readValue(json(Double.class, DOUBLE), PropertyModel.class);

        assertThat(model).isEqualToComparingFieldByField(new PropertyModel(NAME, "Double", DOUBLE));
    }

    @Test
    public void doubleType_notDoubleValue_throwsJsonParse() {
        for (Object value : new Object[]{BOOLEAN, STRING, OBJECT, ARRAY}) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json(Double.class, value), PropertyModel.class));
        }
    }

    @Test
    public void stringProperty() throws Exception {
        PropertyModel model = objectMapper.readValue(json(String.class, STRING), PropertyModel.class);

        assertThat(model).isEqualToComparingFieldByField(new PropertyModel(NAME, "String", STRING.replace("\"", "")));
    }

    @Test
    public void stringType_notStringValue_throwsJsonParse() {
        for (Object value : new Object[]{BOOLEAN, SHORT, INT, LONG, FLOAT, DOUBLE, OBJECT, ARRAY}) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json(String.class, value), PropertyModel.class));
        }
    }

    @Test
    public void missingProperty_throwsJsonParse() {
        String[] missingProperty = {
                "{}",

                "{\"name\": \"property 1\"}",
                "{\"type\": \"Float\"}",
                "{\"value\": \"hello world\"}",

                "{\"name\": \"property 2\", \"type\": \"Float\"}",
                "{\"name\": \"property 2\", \"value\": \"hello world\"}",
                "{\"type\": \"Float\", \"value\": 1.2}",
        };

        for (String json : missingProperty) {
            assertThatExceptionOfType(JsonParseException.class)
                    .isThrownBy(() -> objectMapper.readValue(json, PropertyModel.class));
        }
    }

    @Test
    public void extraProperty_throwsJsonParse() {
        String extraProperty = "{\"name\": \"property 8\", \"type\": \"String\", \"value\": \"hello world\", \"other\": 2}";
        assertThatExceptionOfType(JsonParseException.class).isThrownBy(() -> objectMapper.readValue(extraProperty, PropertyModel.class));
    }

    @Test
    public void notJson_throwsJsonParse() {
        String[] notJson = {
                "hello world",
                "{hello world}",
                "[type: Float, value: 2.3, name: my prop]"
        };

        for (String string : notJson) {
            assertThatExceptionOfType(JsonParseException.class).isThrownBy(() -> objectMapper.readValue(string, PropertyModel.class));
        }
    }

    @Test
    public void invalidValueTypes_throwsJsonParse() {
        String[] invalidValueTypes = {
                "{\"name\": 2, \"type\": \"String\", \"value\": \"hello world\"}",
                "{\"name\": \"property 8\", \"type\": false, \"value\": \"hello world\"}",
                "{\"name\": \"property 8\", \"type\": \"String\", \"value\": []}",
        };

        for (String string : invalidValueTypes) {
            assertThatExceptionOfType(JsonParseException.class).isThrownBy(() -> objectMapper.readValue(string, PropertyModel.class));
        }
    }

    private static String json(Class<?> type, Object value) {
        return "{\"name\": \"" + NAME + "\", \"type\": \"" + type.getSimpleName() + "\", \"value\": " + value + "}";
    }
}