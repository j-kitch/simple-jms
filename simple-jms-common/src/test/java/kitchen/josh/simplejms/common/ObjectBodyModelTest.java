package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class ObjectBodyModelTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void readValue() throws Exception {
        String json = "{\"type\":\"object\",\"object\":\"AQUKDA==\"}";
        ObjectBodyModel expected = new ObjectBodyModel(new byte[]{1, 5, 10, 12});

        ObjectBodyModel actual = objectMapper.readValue(json, ObjectBodyModel.class);

        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void writeValueAsString() throws Exception {
        ObjectBodyModel model = new ObjectBodyModel(new byte[]{1, 5, 10, 12});
        String expected = "{\"type\":\"object\",\"object\":\"AQUKDA==\"}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }
}