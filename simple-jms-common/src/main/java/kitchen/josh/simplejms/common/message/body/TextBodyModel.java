package kitchen.josh.simplejms.common.message.body;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TextBodyModel extends BodyModel {

    private final String text;

    public TextBodyModel(@JsonProperty("text") String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextBodyModel that = (TextBodyModel) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
