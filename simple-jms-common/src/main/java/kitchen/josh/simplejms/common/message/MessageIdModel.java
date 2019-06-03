package kitchen.josh.simplejms.common.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;

public class MessageIdModel {

    private final String id;

    @JsonCreator
    public MessageIdModel(String id) {
        this.id = id;
    }

    @JsonGetter
    public String getId() {
        return id;
    }
}
