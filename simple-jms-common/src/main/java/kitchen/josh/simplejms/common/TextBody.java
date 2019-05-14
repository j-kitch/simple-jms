package kitchen.josh.simplejms.common;

import javax.jms.MessageFormatException;

public class TextBody {

    private String text;

    public void clearBody() {
        this.text = null;
    }

    public <T> T getBody(Class<T> type) throws MessageFormatException {
        try {
            return type.cast(text);
        } catch (ClassCastException e) {
            throw new MessageFormatException(e.getMessage());
        }
    }

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
