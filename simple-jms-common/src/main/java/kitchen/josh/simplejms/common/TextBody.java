package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;

public class TextBody implements Body {

    private String text;

    public TextBody() {

    }

    public TextBody(String text) {
        this.text = text;
    }

    @Override
    public void clearBody() {
        this.text = null;
    }

    @Override
    public <T> T getBody(Class<T> type) throws MessageFormatException {
        try {
            return type.cast(text);
        } catch (ClassCastException e) {
            throw new MessageFormatException(e.getMessage());
        }
    }

    @Override
    public boolean isBodyAssignableTo(Class c) {
        if (text == null) {
            return true;
        }
        return c.isInstance(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
