package kitchen.josh.simplejms.common.message.properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class PropertyModelTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void writeValueAsString() throws Exception {
        PropertyModel model = new PropertyModel("property", "Float", 1.2f);
        String expected = "{\"name\": \"property\", \"type\": \"Float\", \"value\": 1.2}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }
}