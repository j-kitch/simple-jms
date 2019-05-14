package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;
import java.io.*;

public class ObjectBody {

    private byte[] bytes;

    public void clearBody() {
        bytes = null;
    }

    public <T> T getBody(Class<T> type) throws MessageFormatException {
        try {
            return type.cast(getObject());
        } catch (ClassCastException e) {
            throw new MessageFormatException(e.getMessage());
        }
    }

    public boolean isBodyAssignableTo(Class c) {
        if (bytes == null) {
            return true;
        }
        return c.isInstance(getObject());
    }

    public void setObject(Serializable serializable) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(serializable);
            objectOutputStream.flush();
            this.bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Serializable getObject() {
        if (bytes == null) {
            return null;
        }
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(this.bytes))) {
            return (Serializable) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
