package kitchen.josh.simplejms.common;

public interface Message extends Headers, Properties, Body {

    Headers getHeaders();

    Body getBody();
}
