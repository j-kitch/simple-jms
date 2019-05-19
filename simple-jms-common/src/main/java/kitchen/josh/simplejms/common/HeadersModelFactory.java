package kitchen.josh.simplejms.common;

import java.util.Optional;

public class HeadersModelFactory {

    public HeadersModel create(Headers headers) {
        return new HeadersModel(headers.getId(), convert(headers).orElse(null));
    }

    private static Optional<String> convert(Headers headers) {
        return Optional.ofNullable(headers)
                .map(Headers::getDestination)
                .map(destination -> destination.getType().name().toLowerCase() + ":" + destination.getId());
    }
}
