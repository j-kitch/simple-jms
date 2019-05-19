package kitchen.josh.simplejms.common;

public interface Message extends Headers, Properties, Body {

    Headers getHeaders();

    Properties getProperties();

    Body getBody();
}
