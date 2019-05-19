package kitchen.josh.simplejms.common;

public interface Message extends Headers, Properties, Body {

    Body getBody();
}
