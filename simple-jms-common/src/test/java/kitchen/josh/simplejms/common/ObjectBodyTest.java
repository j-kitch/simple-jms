package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;

import javax.jms.MessageFormatException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ObjectBodyTest {

    private ObjectBody objectBody;

    @Before
    public void setUp() {
        objectBody = new ObjectBody();
    }

    @Test
    public void setObject_canReadObject() {
        objectBody.setObject(2);

        assertThat(objectBody.getObject()).isEqualTo(2);
    }

    @Test
    public void clearBody_objectIsNull() {
        objectBody.setObject(2);

        objectBody.clearBody();

        assertThat(objectBody.getObject()).isNull();
    }

    @Test
    public void getBody_hasObject_returnsForSerializableAndAssignable() throws MessageFormatException {
        objectBody.setObject(2);

        assertThat(objectBody.getBody(Serializable.class)).isEqualTo(2);
        assertThat(objectBody.getBody(Number.class)).isEqualTo(2);
        assertThat(objectBody.getBody(Comparable.class)).isEqualTo(2);
        assertThat(objectBody.getBody(Integer.class)).isEqualTo(2);
    }

    @Test
    public void getBody_hasObject_throwsMessageFormatForNonAssignable() {
        objectBody.setObject(2);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> objectBody.getBody(String.class));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> objectBody.getBody(Float.class));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> objectBody.getBody(UUID.class));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> objectBody.getBody(List.class));
    }

    @Test
    public void getBody_noObject_returnsNull() throws MessageFormatException {
        assertThat(objectBody.getBody(Serializable.class)).isNull();
        assertThat(objectBody.getBody(Number.class)).isNull();
        assertThat(objectBody.getBody(Comparable.class)).isNull();
        assertThat(objectBody.getBody(Integer.class)).isNull();

        assertThat(objectBody.getBody(String.class)).isNull();
        assertThat(objectBody.getBody(Float.class)).isNull();
        assertThat(objectBody.getBody(UUID.class)).isNull();
        assertThat(objectBody.getBody(List.class)).isNull();
    }

    @Test
    public void isBodyAssignable_hasObject_returnsTrueForAssignable() {
        objectBody.setObject(2);

        assertThat(objectBody.isBodyAssignableTo(Serializable.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(Number.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(Comparable.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(Integer.class)).isTrue();
    }

    @Test
    public void isBodyAssignable_hasObject_returnsFalseForNonAssignable() {
        objectBody.setObject(2);

        assertThat(objectBody.isBodyAssignableTo(String.class)).isFalse();
        assertThat(objectBody.isBodyAssignableTo(Float.class)).isFalse();
        assertThat(objectBody.isBodyAssignableTo(UUID.class)).isFalse();
        assertThat(objectBody.isBodyAssignableTo(List.class)).isFalse();
    }

    @Test
    public void isBodyAssignable_noObject_returnsTrue() {
        assertThat(objectBody.isBodyAssignableTo(Serializable.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(Number.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(Comparable.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(Integer.class)).isTrue();

        assertThat(objectBody.isBodyAssignableTo(String.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(Float.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(UUID.class)).isTrue();
        assertThat(objectBody.isBodyAssignableTo(List.class)).isTrue();
    }
}