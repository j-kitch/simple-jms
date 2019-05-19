package kitchen.josh.simplejms.common;

public class HeadersImpl implements Headers {

    private String id;
    private Destination destination;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Destination getDestination() {
        return destination;
    }

    @Override
    public void setDestination(Destination destination) {
        this.destination = destination;
    }
}
