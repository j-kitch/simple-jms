package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static java.util.Arrays.asList;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class ObjectMapperTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void idModel() throws Exception {
        UUID id = UUID.randomUUID();
        IdModel model = new IdModel(id);
        String expected = "{\"id\": \"" + id + "\"}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }

    @Test
    public void propertyModel() throws Exception {
        PropertyModel model = new PropertyModel("property", "Float", 1.2f);
        String expected = "{\"name\": \"property\", \"type\": \"Float\", \"value\": 1.2}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }

    @Test
    public void messageModel() throws Exception {
        MessageModel model = new MessageModel(asList(
                new PropertyModel("property 1", "Double", 2.3),
                new PropertyModel("property 2", "Boolean", false)),
                "hello world");
        String expected = "{\"properties\": [" +
                "{\"name\": \"property 1\", \"type\": \"Double\", \"value\": 2.3}," +
                "{\"name\": \"property 2\", \"type\": \"Boolean\", \"value\": false}" +
                "], \"message\": \"hello world\"}";

        String actual = objectMapper.writeValueAsString(model);


        assertEquals(expected, actual, true);
    }

    @Test
    public void errorModel() throws Exception {
        ErrorModel model = new ErrorModel("error message");
        String expected = "{\"message\": \"error message\"}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }
}