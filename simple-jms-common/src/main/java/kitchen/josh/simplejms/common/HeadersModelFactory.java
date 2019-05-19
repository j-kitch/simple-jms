package kitchen.josh.simplejms.common;

public class HeadersModelFactory {

    public HeadersModel create(Headers headers) {
        return new HeadersModel(headers.getId(),
                headers.getDestination().getType().name().toLowerCase() + ":" + headers.getDestination().getId());
    }
}
