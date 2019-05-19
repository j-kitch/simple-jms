package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class HeadersModelTest {

    private static final UUID ID = UUID.randomUUID();
    private static final UUID TOPIC = UUID.randomUUID();

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void readValue() throws Exception {
        String json = "{\"JMSMessageID\": \"ID:" + ID + "\", \"JMSDestination\": \"topic:" + TOPIC + "\"}";
        HeadersModel expected = new HeadersModel("ID:" + ID, "topic:" + TOPIC);

        HeadersModel actual = objectMapper.readValue(json, HeadersModel.class);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void writeValueAsString() throws Exception {
        HeadersModel model = new HeadersModel("ID:" + ID, "topic:" + TOPIC);
        String expected = "{\"JMSMessageID\": \"ID:" + ID + "\", \"JMSDestination\": \"topic:" + TOPIC + "\"}";

        String actual = objectMapper.writeValueAsString(model);

        assertEquals(expected, actual, true);
    }
}