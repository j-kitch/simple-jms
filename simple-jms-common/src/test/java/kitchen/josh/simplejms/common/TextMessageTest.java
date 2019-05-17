package kitchen.josh.simplejms.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import java.util.Enumeration;

import static java.util.Collections.enumeration;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TextMessageTest {

    @Mock
    private Properties properties;

    @Mock
    private TextBody textBody;

    private TextMessage textMessage;

    @Before
    public void setUp() {
        textMessage = new TextMessage(properties, textBody);
    }

    @Test
    public void clearProperties() {
        textMessage.clearProperties();

        verify(properties).clearProperties();
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getBooleanProperty() throws JMSException {
        when(properties.getBooleanProperty(any())).thenReturn(true);

        assertThat(textMessage.getBooleanProperty("property")).isTrue();

        verify(properties).getBooleanProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getBooleanProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getBooleanProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> textMessage.getBooleanProperty("property"));

        verify(properties).getBooleanProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setBooleanProperty() {
        textMessage.setBooleanProperty("property", true);

        verify(properties).setBooleanProperty("property", true);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getByteProperty() throws JMSException {
        when(properties.getByteProperty(any())).thenReturn((byte) 1);

        assertThat(textMessage.getByteProperty("property")).isEqualTo((byte) 1);

        verify(properties).getByteProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getByteProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getByteProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> textMessage.getByteProperty("property"));

        verify(properties).getByteProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setByteProperty() {
        textMessage.setByteProperty("property", (byte) 2);

        verify(properties).setByteProperty("property", (byte) 2);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getShortProperty() throws JMSException {
        when(properties.getShortProperty(any())).thenReturn((short) 3);

        assertThat(textMessage.getShortProperty("property")).isEqualTo((short) 3);

        verify(properties).getShortProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getShortProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getShortProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> textMessage.getShortProperty("property"));

        verify(properties).getShortProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setShortProperty() {
        textMessage.setShortProperty("property", (short) 3);

        verify(properties).setShortProperty("property", (short) 3);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getIntProperty() throws JMSException {
        when(properties.getIntProperty(any())).thenReturn(5);

        assertThat(textMessage.getIntProperty("property")).isEqualTo(5);

        verify(properties).getIntProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getIntProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getIntProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> textMessage.getIntProperty("property"));

        verify(properties).getIntProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setIntProperty() {
        textMessage.setIntProperty("property", 6);

        verify(properties).setIntProperty("property", 6);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getLongProperty() throws JMSException {
        when(properties.getLongProperty(any())).thenReturn(6L);

        assertThat(textMessage.getLongProperty("property")).isEqualTo(6L);

        verify(properties).getLongProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getLongProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getLongProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> textMessage.getLongProperty("property"));

        verify(properties).getLongProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setLongProperty() {
        textMessage.setLongProperty("property", 7L);

        verify(properties).setLongProperty("property", 7L);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getFloatProperty() throws JMSException {
        when(properties.getFloatProperty(any())).thenReturn(1.23f);

        assertThat(textMessage.getFloatProperty("property")).isEqualTo(1.23f);

        verify(properties).getFloatProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getFloatProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getFloatProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> textMessage.getFloatProperty("property"));

        verify(properties).getFloatProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setFloatProperty() {
        textMessage.setFloatProperty("property", 1.23f);

        verify(properties).setFloatProperty("property", 1.23f);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getDoubleProperty() throws JMSException {
        when(properties.getDoubleProperty(any())).thenReturn(2.34);

        assertThat(textMessage.getDoubleProperty("property")).isEqualTo(2.34);

        verify(properties).getDoubleProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getDoubleProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getDoubleProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> textMessage.getDoubleProperty("property"));

        verify(properties).getDoubleProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setDoubleProperty() {
        textMessage.setDoubleProperty("property", 2.34);

        verify(properties).setDoubleProperty("property", 2.34);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getStringProperty() {
        when(properties.getStringProperty(any())).thenReturn("hello world");

        assertThat(textMessage.getStringProperty("property")).isEqualTo("hello world");

        verify(properties).getStringProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setStringProperty() {
        textMessage.setStringProperty("property", "hello world");

        verify(properties).setStringProperty("property", "hello world");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getObjectProperty() {
        when(properties.getObjectProperty(any())).thenReturn(2);

        assertThat(textMessage.getObjectProperty("property")).isEqualTo(2);

        verify(properties).getObjectProperty("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setObjectProperty() throws JMSException {
        textMessage.setObjectProperty("property", 2);

        verify(properties).setObjectProperty("property", 2);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getPropertyNames() {
        Enumeration<String> propertyNames = enumeration(singleton("hello"));
        when(properties.getPropertyNames()).thenReturn(propertyNames);

        assertThat(textMessage.getPropertyNames()).isEqualTo(propertyNames);

        verify(properties).getPropertyNames();
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void propertyExists() {
        when(properties.propertyExists(any())).thenReturn(true);

        assertThat(textMessage.propertyExists("property")).isTrue();

        verify(properties).propertyExists("property");
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getBody() throws Exception {
        when(textBody.getBody(any())).thenReturn(2);

        assertThat(textMessage.getBody(int.class)).isEqualTo(2);

        verify(textBody).getBody(int.class);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getBody_throwsMessageFormat_throwsMessageFormat() throws MessageFormatException {
        when(textBody.getBody(any())).thenThrow(MessageFormatException.class);

        assertThatExceptionOfType(MessageFormatException.class)
                .isThrownBy(() -> textMessage.getBody(int.class));

        verify(textBody).getBody(int.class);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void isBodyAssignableTo() {
        when(textBody.isBodyAssignableTo(any())).thenReturn(true);

        assertThat(textMessage.isBodyAssignableTo(int.class)).isTrue();

        verify(textBody).isBodyAssignableTo(int.class);
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void getText() {
        when(textBody.getText()).thenReturn("hello world");

        assertThat(textMessage.getText()).isEqualTo("hello world");

        verify(textBody).getText();
        verifyNoMoreInteractions(properties, textBody);
    }

    @Test
    public void setText() {
        textMessage.setText("hello world");

        verify(textBody).setText("hello world");
        verifyNoMoreInteractions(properties, textBody);
    }
}