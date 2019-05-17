package kitchen.josh.simplejms.common;

import javax.jms.JMSException;
import java.util.Enumeration;

public interface Properties {

    void clearProperties();

    boolean getBooleanProperty(String name) throws JMSException;

    void setBooleanProperty(String name, boolean value);

    byte getByteProperty(String name) throws JMSException;

    void setByteProperty(String name, byte value);

    short getShortProperty(String name) throws JMSException;

    void setShortProperty(String name, short value);

    int getIntProperty(String name) throws JMSException;

    void setIntProperty(String name, int value);

    long getLongProperty(String name) throws JMSException;

    void setLongProperty(String name, long value);

    float getFloatProperty(String name) throws JMSException;

    void setFloatProperty(String name, float value);

    double getDoubleProperty(String name) throws JMSException;

    void setDoubleProperty(String name, double value);

    String getStringProperty(String name);

    void setStringProperty(String name, String value);

    Object getObjectProperty(String name);

    void setObjectProperty(String name, Object value) throws JMSException;

    Enumeration<String> getPropertyNames();

    boolean propertyExists(String name);
}
