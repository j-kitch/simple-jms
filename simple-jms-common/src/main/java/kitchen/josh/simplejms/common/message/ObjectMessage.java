package kitchen.josh.simplejms.common.message;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.message.body.ObjectBody;
import kitchen.josh.simplejms.common.message.headers.Headers;
import kitchen.josh.simplejms.common.message.properties.Properties;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import java.io.Serializable;
import java.util.Enumeration;

public class ObjectMessage implements Message {

    private final Headers headers;
    private final Properties properties;
    private final ObjectBody body;

    public ObjectMessage(Headers headers, Properties properties, ObjectBody body) {
        this.headers = headers;
        this.properties = properties;
        this.body = body;
    }

    @Override
    public String getId() {
        return headers.getId();
    }

    @Override
    public void setId(String id) {
        headers.setId(id);
    }

    @Override
    public Destination getDestination() {
        return headers.getDestination();
    }

    @Override
    public void setDestination(Destination destination) {
        headers.setDestination(destination);
    }

    @Override
    public void clearProperties() {
        properties.clearProperties();
    }

    @Override
    public boolean getBooleanProperty(String name) throws JMSException {
        return properties.getBooleanProperty(name);
    }

    @Override
    public void setBooleanProperty(String name, boolean value) {
        properties.setBooleanProperty(name, value);
    }

    @Override
    public byte getByteProperty(String name) throws JMSException {
        return properties.getByteProperty(name);
    }

    @Override
    public void setByteProperty(String name, byte value) {
        properties.setByteProperty(name, value);
    }

    @Override
    public short getShortProperty(String name) throws JMSException {
        return properties.getShortProperty(name);
    }

    @Override
    public void setShortProperty(String name, short value) {
        properties.setShortProperty(name, value);
    }

    @Override
    public int getIntProperty(String name) throws JMSException {
        return properties.getIntProperty(name);
    }

    @Override
    public void setIntProperty(String name, int value) {
        properties.setIntProperty(name, value);
    }

    @Override
    public long getLongProperty(String name) throws JMSException {
        return properties.getLongProperty(name);
    }

    @Override
    public void setLongProperty(String name, long value) {
        properties.setLongProperty(name, value);
    }

    @Override
    public float getFloatProperty(String name) throws JMSException {
        return properties.getFloatProperty(name);
    }

    @Override
    public void setFloatProperty(String name, float value) {
        properties.setFloatProperty(name, value);
    }

    @Override
    public double getDoubleProperty(String name) throws JMSException {
        return properties.getDoubleProperty(name);
    }

    @Override
    public void setDoubleProperty(String name, double value) {
        properties.setDoubleProperty(name, value);
    }

    @Override
    public String getStringProperty(String name) {
        return properties.getStringProperty(name);
    }

    @Override
    public void setStringProperty(String name, String value) {
        properties.setStringProperty(name, value);
    }

    @Override
    public Object getObjectProperty(String name) {
        return properties.getObjectProperty(name);
    }

    @Override
    public void setObjectProperty(String name, Object value) throws JMSException {
        properties.setObjectProperty(name, value);
    }

    @Override
    public Enumeration<String> getPropertyNames() {
        return properties.getPropertyNames();
    }

    @Override
    public boolean propertyExists(String name) {
        return properties.propertyExists(name);
    }

    @Override
    public void clearBody() {
        body.clearBody();
    }

    @Override
    public <T> T getBody(Class<T> type) throws MessageFormatException {
        return body.getBody(type);
    }

    @Override
    public boolean isBodyAssignableTo(Class c) {
        return body.isBodyAssignableTo(c);
    }

    public void setObject(Serializable serializable) {
        body.setObject(serializable);
    }

    public Serializable getObject() {
        return body.getObject();
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public ObjectBody getBody() {
        return body;
    }
}
