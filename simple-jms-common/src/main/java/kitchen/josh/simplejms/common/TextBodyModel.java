package kitchen.josh.simplejms.common;

import java.util.Objects;

public class TextBodyModel extends BodyModel {

    private String text;

    public TextBodyModel() {

    }

    public TextBodyModel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
