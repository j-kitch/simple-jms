package kitchen.josh.simplejms.common.message;

import kitchen.josh.simplejms.common.message.body.Body;
import kitchen.josh.simplejms.common.message.headers.Headers;
import kitchen.josh.simplejms.common.message.properties.Properties;

public interface Message extends Headers, Properties, Body {

    Headers getHeaders();

    Properties getProperties();

    Body getBody();
}
