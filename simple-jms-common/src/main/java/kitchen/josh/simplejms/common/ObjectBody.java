package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;
import java.io.Serializable;

import static org.springframework.util.SerializationUtils.deserialize;
import static org.springframework.util.SerializationUtils.serialize;

public class ObjectBody implements Body {

    private byte[] bytes;

    public ObjectBody() {

    }

    public ObjectBody(Serializable serializable) {
        setObject(serializable);
    }

    @Override
    public void clearBody() {
        bytes = null;
    }

    @Override
    public <T> T getBody(Class<T> type) throws MessageFormatException {
        try {
            return type.cast(getObject());
        } catch (ClassCastException e) {
            throw new MessageFormatException(e.getMessage());
        }
    }

    @Override
    public boolean isBodyAssignableTo(Class c) {
        if (bytes == null) {
            return true;
        }
        return c.isInstance(getObject());
    }

    public void setObject(Serializable serializable) {
        this.bytes = serialize(serializable);
    }

    public Serializable getObject() {
        return (Serializable) deserialize(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }
}
