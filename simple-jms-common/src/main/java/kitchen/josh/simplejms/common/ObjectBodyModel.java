package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Base64;

@JsonIgnoreProperties("bytes")
public class ObjectBodyModel extends BodyModel {

    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private byte[] bytes;

    public ObjectBodyModel() {

    }

    public ObjectBodyModel(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @JsonGetter("object")
    public String getBase64Bytes() {
        return ENCODER.encodeToString(bytes);
    }

    @JsonSetter("object")
    public void setBase64Bytes(String encoded) {
        this.bytes = DECODER.decode(encoded);
    }
}
