package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class ErrorModelTest {

    private static final String MESSAGE = "hello world";

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void readValue() throws Exception {
        String json = "{\"message\": \"" + MESSAGE + "\"}";
        ErrorModel expected = new ErrorModel(MESSAGE);

        ErrorModel actual = objectMapper.readValue(json, ErrorModel.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void writeValueAsString() throws Exception {
        ErrorModel model = new ErrorModel(MESSAGE);
        String expected = "{\"message\": \"" + MESSAGE + "\"}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }
}