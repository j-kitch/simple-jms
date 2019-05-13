package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

@JsonDeserialize(using = PropertyModelDeserializer.class)
public class PropertyModel {

    private String name;
    private String type;
    private Object value;

    public PropertyModel() {

    }

    public PropertyModel(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyModel model = (PropertyModel) o;
        return Objects.equals(name, model.name) &&
                Objects.equals(type, model.type) &&
                Objects.equals(value, model.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value);
    }
}