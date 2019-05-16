package kitchen.josh.simplejms.common;

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
}
