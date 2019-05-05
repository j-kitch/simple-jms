package kitchen.josh.simplejms.common;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import java.util.*;

import static java.util.Collections.enumeration;

public class Properties {

    private static final List<Class<?>> PROPERTY_TYPES = Arrays.asList(
            Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, String.class);

    private final Map<String, Object> properties;

    public Properties() {
        this.properties = new HashMap<>();
    }

    public void clearProperties() {
        properties.clear();
    }

    public boolean getBooleanProperty(String name) throws JMSException {
        Object value = properties.get(name);
        if (value == null) {
            return Boolean.valueOf(null);
        }
        if (value.getClass() == Boolean.class) {
            return (boolean) value;
        }
        if (value.getClass() == String.class) {
            return Boolean.valueOf((String) value);
        }
        throw new MessageFormatException("");
    }

    public void setBooleanProperty(String name, boolean value) {
        properties.put(name, value);
    }

    public byte getByteProperty(String name) throws JMSException {
        Object value = properties.get(name);
        if (value == null) {
            return Byte.valueOf(null);
        }
        if (value.getClass() == Byte.class) {
            return (byte) value;
        }
        if (value.getClass() == String.class) {
            return Byte.valueOf((String) value);
        }
        throw new MessageFormatException("");
    }

    public void setByteProperty(String name, byte value) {
        properties.put(name, value);
    }

    public short getShortProperty(String name) throws JMSException {
        Object value = properties.get(name);
        if (value == null) {
            return Short.valueOf(null);
        }
        if (value.getClass() == Byte.class) {
            return (byte) value;
        }
        if (value.getClass() == Short.class) {
            return (short) value;
        }
        if (value.getClass() == String.class) {
            return Short.valueOf((String) value);
        }
        throw new MessageFormatException("");
    }

    public void setShortProperty(String name, short value) {
        properties.put(name, value);
    }

    public int getIntProperty(String name) throws JMSException {
        Object value = properties.get(name);
        if (value == null) {
            return Integer.valueOf(null);
        }
        if (value.getClass() == Byte.class) {
            return (byte) value;
        }
        if (value.getClass() == Short.class) {
            return (short) value;
        }
        if (value.getClass() == Integer.class) {
            return (int) value;
        }
        if (value.getClass() == String.class) {
            return Integer.valueOf((String) value);
        }
        throw new MessageFormatException("");
    }

    public void setIntProperty(String name, int value) {
        properties.put(name, value);
    }

    public long getLongProperty(String name) throws JMSException {
        Object value = properties.get(name);
        if (value == null) {
            return Long.valueOf(null);
        }
        if (value.getClass() == Byte.class) {
            return (byte) value;
        }
        if (value.getClass() == Short.class) {
            return (short) value;
        }
        if (value.getClass() == Integer.class) {
            return (int) value;
        }
        if (value.getClass() == Long.class) {
            return (long) value;
        }
        if (value.getClass() == String.class) {
            return Long.valueOf((String) value);
        }
        throw new MessageFormatException("");
    }

    public void setLongProperty(String name, long value) {
        properties.put(name, value);
    }

    public float getFloatProperty(String name) throws JMSException {
        Object value = properties.get(name);
        if (value == null) {
            return Float.valueOf(null);
        }
        if (value.getClass() == Float.class) {
            return (float) value;
        }
        if (value.getClass() == String.class) {
            return Float.valueOf((String) value);
        }
        throw new MessageFormatException("");
    }

    public void setFloatProperty(String name, float value) {
        properties.put(name, value);
    }

    public double getDoubleProperty(String name) throws JMSException {
        Object value = properties.get(name);
        if (value == null) {
            return Double.valueOf(null);
        }
        if (value.getClass() == Float.class) {
            return (float) value;
        }
        if (value.getClass() == Double.class) {
            return (double) value;
        }
        if (value.getClass() == String.class) {
            return Double.valueOf((String) value);
        }
        throw new MessageFormatException("");
    }

    public void setDoubleProperty(String name, double value) {
        properties.put(name, value);
    }

    public String getStringProperty(String name) {
        return Optional.ofNullable(properties.get(name))
                .map(Object::toString)
                .orElse(null);
    }

    public void setStringProperty(String name, String value) {
        properties.put(name, value);
    }

    public Object getObjectProperty(String name) {
        return properties.get(name);
    }

    public void setObjectProperty(String name, Object value) throws MessageFormatException {
        if (!PROPERTY_TYPES.contains(value.getClass())) {
            throw new MessageFormatException("");
        }
        properties.put(name, value);
    }

    public Enumeration<String> getPropertyNames() {
        return enumeration(properties.keySet());
    }

    public boolean propertyExists(String name) {
        return properties.containsKey(name);
    }
}
