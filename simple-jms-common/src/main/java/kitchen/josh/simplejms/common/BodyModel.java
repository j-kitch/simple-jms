package kitchen.josh.simplejms.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
        @Type(value = TextBodyModel.class, name = "text"),
        @Type(value = ObjectBodyModel.class, name = "object")})
public abstract class BodyModel {
}
