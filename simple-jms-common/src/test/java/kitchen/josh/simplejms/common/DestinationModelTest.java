package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class DestinationModelTest {

    private static final UUID ID = UUID.randomUUID();

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void readValue() throws Exception {
        String json = "{\"destination\": \"topic:" + ID + "\"}";
        DestinationModel expected = new DestinationModel(new Destination(DestinationType.TOPIC, ID));

        DestinationModel actual = objectMapper.readValue(json, DestinationModel.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void readValue_invalidJson_throws() {
        String[] invalid = {
                "{}",
                "{\"destination\": \"test:\"}",
                "{\"destination\": \"topic:abc\"}"
        };

        for (String json : invalid) {
            assertThatExceptionOfType(JsonProcessingException.class).isThrownBy(() -> objectMapper.readValue(json, DestinationModel.class));
        }
    }

    @Test
    public void writeValueAsString() throws Exception {
        DestinationModel model = new DestinationModel(new Destination(DestinationType.QUEUE, ID));
        String expected = "{\"destination\": \"queue:" + ID + "\"}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }

    @Test
    public void writeValueAsString_nullDestination_throws() {
        DestinationModel model = new DestinationModel((Destination) null);

        assertThatExceptionOfType(JsonProcessingException.class).isThrownBy(() -> objectMapper.writeValueAsString(model));
    }
}