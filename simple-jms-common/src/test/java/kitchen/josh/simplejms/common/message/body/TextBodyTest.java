package kitchen.josh.simplejms.common.message.body;

import org.junit.Before;
import org.junit.Test;

import javax.jms.MessageFormatException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TextBodyTest {

    private static final String TEXT = "hello world";

    private TextBody textBody;

    @Before
    public void setUp() {
        textBody = new TextBody();
    }

    @Test
    public void clearBody_textIsNull() {
        textBody.setText(TEXT);

        textBody.clearBody();

        assertThat(textBody.getText()).isNull();
    }

    @Test
    public void getBody_hasText_returnsTextForStringAndStringAssignable() throws MessageFormatException {
        textBody.setText(TEXT);

        assertThat(textBody.getBody(String.class)).isEqualTo(TEXT);
        assertThat(textBody.getBody(Serializable.class)).isEqualTo(TEXT);
        assertThat(textBody.getBody(Comparable.class)).isEqualTo(TEXT);
        assertThat(textBody.getBody(CharSequence.class)).isEqualTo(TEXT);
        assertThat(textBody.getBody(Object.class)).isEqualTo(TEXT);
    }

    @Test
    public void getBody_hasText_throwsMessageFormatForNonStringAssignable() {
        textBody.setText(TEXT);

        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> textBody.getBody(int.class));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> textBody.getBody(Float.class));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> textBody.getBody(UUID.class));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> textBody.getBody(LocalDateTime.class));
        assertThatExceptionOfType(MessageFormatException.class).isThrownBy(() -> textBody.getBody(List.class));
    }

    @Test
    public void getBody_noText_returnsNullForAnyType() throws MessageFormatException {
        assertThat(textBody.getBody(String.class)).isNull();
        assertThat(textBody.getBody(Serializable.class)).isNull();
        assertThat(textBody.getBody(Comparable.class)).isNull();
        assertThat(textBody.getBody(CharSequence.class)).isNull();
        assertThat(textBody.getBody(Object.class)).isNull();

        assertThat(textBody.getBody(int.class)).isNull();
        assertThat(textBody.getBody(Float.class)).isNull();
        assertThat(textBody.getBody(UUID.class)).isNull();
        assertThat(textBody.getBody(LocalDateTime.class)).isNull();
        assertThat(textBody.getBody(List.class)).isNull();
    }

    @Test
    public void isBodyAssignable_hasText_returnsTrueForStringAndStringAssignable() {
        textBody.setText(TEXT);

        assertThat(textBody.isBodyAssignableTo(String.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(Serializable.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(Comparable.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(CharSequence.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(Object.class)).isTrue();
    }

    @Test
    public void isBodyAssignable_hasText_returnsFalseForNonStringAssignable() {
        textBody.setText(TEXT);

        assertThat(textBody.isBodyAssignableTo(int.class)).isFalse();
        assertThat(textBody.isBodyAssignableTo(Float.class)).isFalse();
        assertThat(textBody.isBodyAssignableTo(UUID.class)).isFalse();
        assertThat(textBody.isBodyAssignableTo(LocalDateTime.class)).isFalse();
        assertThat(textBody.isBodyAssignableTo(List.class)).isFalse();
    }

    @Test
    public void isBodyAssignable_noText_returnsTrue() {
        assertThat(textBody.isBodyAssignableTo(String.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(Serializable.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(Comparable.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(CharSequence.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(Object.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(int.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(Float.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(UUID.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(LocalDateTime.class)).isTrue();
        assertThat(textBody.isBodyAssignableTo(List.class)).isTrue();
    }
}