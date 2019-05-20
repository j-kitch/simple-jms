package kitchen.josh.simplejms.common.message.body;

import javax.jms.MessageFormatException;

public interface Body {

    void clearBody();

    <T> T getBody(Class<T> type) throws MessageFormatException;

    boolean isBodyAssignableTo(Class c);
}
