package kitchen.josh.simplejms.common.message.headers;

import kitchen.josh.simplejms.common.Destination;
import kitchen.josh.simplejms.common.DestinationType;

import java.util.UUID;

public class HeadersFactory {

    public Headers create(HeadersModel model) {
        Headers headers = new HeadersImpl();
        headers.setId(model.getId());
        headers.setDestination(parseDestination(model.getDestination()));
        return headers;
    }

    private static Destination parseDestination(String value) {
        if (value == null) {
            return null;
        }
        String[] parts = value.split(":");
        return new Destination(DestinationType.valueOf(parts[0].toUpperCase()), UUID.fromString(parts[1]));
    }
}
