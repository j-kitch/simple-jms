package kitchen.josh.simplejms.common.message.headers;

import kitchen.josh.simplejms.common.Destination;

public interface Headers {

    String getId();

    void setId(String id);

    Destination getDestination();

    void setDestination(Destination destination);
}
