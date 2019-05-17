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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ObjectMessageTest {

    @Mock
    private Properties properties;

    @Mock
    private ObjectBody objectBody;

    private ObjectMessage objectMessage;

    @Before
    public void setUp() {
        objectMessage = new ObjectMessage(properties, objectBody);
    }

    @Test
    public void clearProperties() {
        objectMessage.clearProperties();

        verify(properties).clearProperties();
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getBooleanProperty() throws JMSException {
        when(properties.getBooleanProperty(any())).thenReturn(true);

        assertThat(objectMessage.getBooleanProperty("property")).isTrue();

        verify(properties).getBooleanProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getBooleanProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getBooleanProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> objectMessage.getBooleanProperty("property"));

        verify(properties).getBooleanProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setBooleanProperty() {
        objectMessage.setBooleanProperty("property", true);

        verify(properties).setBooleanProperty("property", true);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getByteProperty() throws JMSException {
        when(properties.getByteProperty(any())).thenReturn((byte) 1);

        assertThat(objectMessage.getByteProperty("property")).isEqualTo((byte) 1);

        verify(properties).getByteProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getByteProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getByteProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> objectMessage.getByteProperty("property"));

        verify(properties).getByteProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setByteProperty() {
        objectMessage.setByteProperty("property", (byte) 2);

        verify(properties).setByteProperty("property", (byte) 2);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getShortProperty() throws JMSException {
        when(properties.getShortProperty(any())).thenReturn((short) 3);

        assertThat(objectMessage.getShortProperty("property")).isEqualTo((short) 3);

        verify(properties).getShortProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getShortProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getShortProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> objectMessage.getShortProperty("property"));

        verify(properties).getShortProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setShortProperty() {
        objectMessage.setShortProperty("property", (short) 3);

        verify(properties).setShortProperty("property", (short) 3);
    }

    @Test
    public void getIntProperty() throws JMSException {
        when(properties.getIntProperty(any())).thenReturn(5);

        assertThat(objectMessage.getIntProperty("property")).isEqualTo(5);

        verify(properties).getIntProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getIntProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getIntProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> objectMessage.getIntProperty("property"));

        verify(properties).getIntProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setIntProperty() {
        objectMessage.setIntProperty("property", 6);

        verify(properties).setIntProperty("property", 6);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getLongProperty() throws JMSException {
        when(properties.getLongProperty(any())).thenReturn(6L);

        assertThat(objectMessage.getLongProperty("property")).isEqualTo(6L);

        verify(properties).getLongProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getLongProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getLongProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> objectMessage.getLongProperty("property"));

        verify(properties).getLongProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setLongProperty() {
        objectMessage.setLongProperty("property", 7L);

        verify(properties).setLongProperty("property", 7L);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getFloatProperty() throws JMSException {
        when(properties.getFloatProperty(any())).thenReturn(1.23f);

        assertThat(objectMessage.getFloatProperty("property")).isEqualTo(1.23f);

        verify(properties).getFloatProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getFloatProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getFloatProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> objectMessage.getFloatProperty("property"));

        verify(properties).getFloatProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setFloatProperty() {
        objectMessage.setFloatProperty("property", 1.23f);

        verify(properties).setFloatProperty("property", 1.23f);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getDoubleProperty() throws JMSException {
        when(properties.getDoubleProperty(any())).thenReturn(2.34);

        assertThat(objectMessage.getDoubleProperty("property")).isEqualTo(2.34);

        verify(properties).getDoubleProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getDoubleProperty_throwsJms_throwsJms() throws JMSException {
        when(properties.getDoubleProperty(any())).thenThrow(JMSException.class);

        assertThatExceptionOfType(JMSException.class)
                .isThrownBy(() -> objectMessage.getDoubleProperty("property"));

        verify(properties).getDoubleProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setDoubleProperty() {
        objectMessage.setDoubleProperty("property", 2.34);

        verify(properties).setDoubleProperty("property", 2.34);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getStringProperty() {
        when(properties.getStringProperty(any())).thenReturn("hello world");

        assertThat(objectMessage.getStringProperty("property")).isEqualTo("hello world");

        verify(properties).getStringProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setStringProperty() {
        objectMessage.setStringProperty("property", "hello world");

        verify(properties).setStringProperty("property", "hello world");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getObjectProperty() {
        when(properties.getObjectProperty(any())).thenReturn(2);

        assertThat(objectMessage.getObjectProperty("property")).isEqualTo(2);

        verify(properties).getObjectProperty("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setObjectProperty() throws JMSException {
        objectMessage.setObjectProperty("property", 2);

        verify(properties).setObjectProperty("property", 2);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getPropertyNames() {
        Enumeration<String> propertyNames = enumeration(singleton("hello"));
        when(properties.getPropertyNames()).thenReturn(propertyNames);

        assertThat(objectMessage.getPropertyNames()).isEqualTo(propertyNames);

        verify(properties).getPropertyNames();
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void propertyExists() {
        when(properties.propertyExists(any())).thenReturn(true);

        assertThat(objectMessage.propertyExists("property")).isTrue();

        verify(properties).propertyExists("property");
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void clearBody() {
        objectMessage.clearBody();

        verify(objectBody).clearBody();
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getBody() throws Exception {
        when(objectBody.getBody(any())).thenReturn(2);

        assertThat(objectMessage.getBody(int.class)).isEqualTo(2);

        verify(objectBody).getBody(int.class);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getBody_throwsMessageFormat_throwsMessageFormat() throws MessageFormatException {
        when(objectBody.getBody(any())).thenThrow(MessageFormatException.class);

        assertThatExceptionOfType(MessageFormatException.class)
                .isThrownBy(() -> objectMessage.getBody(int.class));

        verify(objectBody).getBody(int.class);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void isBodyAssignableTo() {
        when(objectBody.isBodyAssignableTo(any())).thenReturn(true);

        assertThat(objectMessage.isBodyAssignableTo(int.class)).isTrue();

        verify(objectBody).isBodyAssignableTo(int.class);
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void getObject() {
        when(objectBody.getObject()).thenReturn(2);

        assertThat(objectMessage.getObject()).isEqualTo(2);

        verify(objectBody).getObject();
        verifyNoMoreInteractions(properties, objectBody);
    }

    @Test
    public void setObject() {
        objectMessage.setObject(2);

        verify(objectBody).setObject(2);
        verifyNoMoreInteractions(properties, objectBody);
    }
}