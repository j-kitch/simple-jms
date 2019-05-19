package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class MessageModelTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void readValue_textBody() throws Exception {
        String json = "{\"properties\":[" +
                "{\"name\":\"prop a\",\"type\":\"Float\",\"value\":2.3}," +
                "{\"name\":\"b\",\"type\":\"Boolean\",\"value\":true}" +
                "],\"body\":{\"type\":\"text\",\"text\":\"hello world\"}," +
                "\"headers\": {\"JMSMessageID\": null, \"JMSDestination\": null}}";
        MessageModel expected = new MessageModel(
                new HeadersModel(null, null),
                Arrays.asList(new PropertyModel("prop a", "Float", 2.3f),
                        new PropertyModel("b", "Boolean", true)),
                new TextBodyModel("hello world"));

        MessageModel actual = objectMapper.readValue(json, MessageModel.class);

        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void writeValueAsString_textBody() throws Exception {
        MessageModel messageModel = new MessageModel(
                new HeadersModel(null, null),
                Arrays.asList(new PropertyModel("prop a", "Float", 2.3f),
                        new PropertyModel("b", "Boolean", true)),
                new TextBodyModel("hello world"));
        String expected = "{\"properties\":[" +
                "{\"name\":\"prop a\",\"type\":\"Float\",\"value\":2.3}," +
                "{\"name\":\"b\",\"type\":\"Boolean\",\"value\":true}" +
                "],\"body\":{\"type\":\"text\",\"text\":\"hello world\"}," +
                "\"headers\": {\"JMSMessageID\": null, \"JMSDestination\": null}}";

        String actual = objectMapper.writeValueAsString(messageModel);

        assertEquals(expected, actual, true);
    }

    @Test
    public void readValue_objectBody() throws Exception {
        String json = "{\"properties\":[" +
                "{\"name\":\"prop-c\",\"type\":\"Byte\",\"value\":125}," +
                "{\"name\":\"f\",\"type\":\"String\",\"value\":\"\"}" +
                "],\"body\":{\"type\":\"object\",\"object\":\"AQUKDA==\"}," +
                "\"headers\": {\"JMSMessageID\": null, \"JMSDestination\": null}}";
        MessageModel expected = new MessageModel(
                new HeadersModel(null, null),
                Arrays.asList(new PropertyModel("prop-c", "Byte", (byte) 125),
                        new PropertyModel("f", "String", "")),
                new ObjectBodyModel(new byte[]{1, 5, 10, 12}));

        MessageModel actual = objectMapper.readValue(json, MessageModel.class);

        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void writeValueAsString_objectBody() throws Exception {
        MessageModel messageModel = new MessageModel(
                new HeadersModel(null, null),
                Arrays.asList(new PropertyModel("prop-c", "Byte", (byte) 125),
                        new PropertyModel("f", "String", "")),
                new ObjectBodyModel(new byte[]{1, 5, 10, 12}));
        String expected = "{\"properties\":[" +
                "{\"name\":\"prop-c\",\"type\":\"Byte\",\"value\":125}," +
                "{\"name\":\"f\",\"type\":\"String\",\"value\":\"\"}" +
                "],\"body\":{\"type\":\"object\",\"object\":\"AQUKDA==\"}," +
                "\"headers\": {\"JMSMessageID\": null, \"JMSDestination\": null}}";

        String actual = objectMapper.writeValueAsString(messageModel);

        assertEquals(expected, actual, true);
    }
}