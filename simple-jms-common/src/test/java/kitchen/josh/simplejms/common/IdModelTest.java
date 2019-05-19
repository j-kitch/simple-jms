package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class IdModelTest {

    private static final UUID ID = UUID.randomUUID();

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void readValue() throws Exception {
        String json = "{\"id\": \"" + ID + "\"}";
        IdModel expected = new IdModel(ID);

        IdModel actual = objectMapper.readValue(json, IdModel.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void writeValueAsString() throws Exception {
        IdModel model = new IdModel(ID);
        String expected = "{\"id\": \"" + ID + "\"}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }
}