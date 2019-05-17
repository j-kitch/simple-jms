package kitchen.josh.simplejms.common;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import java.util.Enumeration;

public class TextMessage {

    private final Properties properties;
    private final TextBody body;

    public TextMessage(Properties properties, TextBody body) {
        this.properties = properties;
        this.body = body;
    }

    public void clearProperties() {
        properties.clearProperties();
    }

    public boolean getBooleanProperty(String name) throws JMSException {
        return properties.getBooleanProperty(name);
    }

    public void setBooleanProperty(String name, boolean value) {
        properties.setBooleanProperty(name, value);
    }

    public byte getByteProperty(String name) throws JMSException {
        return properties.getByteProperty(name);
    }

    public void setByteProperty(String name, byte value) {
        properties.setByteProperty(name, value);
    }

    public short getShortProperty(String name) throws JMSException {
        return properties.getShortProperty(name);
    }

    public void setShortProperty(String name, short value) {
        properties.setShortProperty(name, value);
    }

    public int getIntProperty(String name) throws JMSException {
        return properties.getIntProperty(name);
    }

    public void setIntProperty(String name, int value) {
        properties.setIntProperty(name, value);
    }

    public long getLongProperty(String name) throws JMSException {
        return properties.getLongProperty(name);
    }

    public void setLongProperty(String name, long value) {
        properties.setLongProperty(name, value);
    }

    public float getFloatProperty(String name) throws JMSException {
        return properties.getFloatProperty(name);
    }

    public void setFloatProperty(String name, float value) {
        properties.setFloatProperty(name, value);
    }

    public double getDoubleProperty(String name) throws JMSException {
        return properties.getDoubleProperty(name);
    }

    public void setDoubleProperty(String name, double value) {
        properties.setDoubleProperty(name, value);
    }

    public String getStringProperty(String name) {
        return properties.getStringProperty(name);
    }

    public void setStringProperty(String name, String value) {
        properties.setStringProperty(name, value);
    }

    public Object getObjectProperty(String name) {
        return properties.getObjectProperty(name);
    }

    public void setObjectProperty(String name, Object value) throws MessageFormatException {
        properties.setObjectProperty(name, value);
    }

    public Enumeration<String> getPropertyNames() {
        return properties.getPropertyNames();
    }

    public boolean propertyExists(String name) {
        return properties.propertyExists(name);
    }

    public <T> T getBody(Class<T> type) throws MessageFormatException {
        return body.getBody(type);
    }

    public boolean isBodyAssignableTo(Class c) {
        return body.isBodyAssignableTo(c);
    }

    public void setText(String text) {
        body.setText(text);
    }

    public String getText() {
        return body.getText();
    }
}
