package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class TextBodyModelTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void readValue() throws Exception {
        String json = "{\"type\":\"text\",\"text\":\"hello world\"}";
        TextBodyModel expected = new TextBodyModel("hello world");

        TextBodyModel actual = objectMapper.readValue(json, TextBodyModel.class);

        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void writeValueAsString() throws Exception {
        TextBodyModel model = new TextBodyModel("hello world");
        String expected = "{\"type\":\"text\",\"text\":\"hello world\"}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }
}