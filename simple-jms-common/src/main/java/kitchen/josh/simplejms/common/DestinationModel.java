package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class DestinationModel {

    private final Destination destination;

    public DestinationModel(Destination destination) {
        this.destination = destination;
    }

    public DestinationModel(@JsonProperty("destination") String value) {
        try {
            String[] parts = value.split(":");
            this.destination = new Destination(DestinationType.valueOf(parts[0].toUpperCase()), UUID.fromString(parts[1]));
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to convert '" + value + "' to Destination");
        }
    }

    public Destination getDestination() {
        return destination;
    }

    @JsonGetter("destination")
    public String getDestinationString() {
        return destination.getType().name().toLowerCase() + ":" + destination.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DestinationModel model = (DestinationModel) o;
        return Objects.equals(destination, model.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination);
    }
}
