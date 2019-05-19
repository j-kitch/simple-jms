package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;

@JsonIgnoreProperties("bytes")
public class ObjectBodyModel extends BodyModel {

    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private final byte[] bytes;

    public ObjectBodyModel(byte[] bytes) {
        this.bytes = bytes;
    }

    public ObjectBodyModel(@JsonProperty("object") String object) {
        this.bytes = DECODER.decode(object);
    }

    public byte[] getBytes() {
        return bytes;
    }

    @JsonGetter("object")
    public String getBase64Bytes() {
        return ENCODER.encodeToString(bytes);
    }
}
