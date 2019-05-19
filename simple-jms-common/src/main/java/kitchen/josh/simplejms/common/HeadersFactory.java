package kitchen.josh.simplejms.common;

import java.util.UUID;

public class HeadersFactory {

    public Headers create(HeadersModel model) {
        Headers headers = new Headers();

        String[] parts = model.getDestination().split(":");
        Destination destination = new Destination(DestinationType.valueOf(parts[0].toUpperCase()), UUID.fromString(parts[1]));

        headers.setId(model.getId());
        headers.setDestination(destination);

        return headers;
    }
}
