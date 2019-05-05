# SimpleJMS 0.5.0

## Messages
This version will introduce message headers, properties and bodies to replace the
old model of plain text messages.

```json
{
  "headers": {},
  "properties": [],
  "body": {}
}
```

### Message Headers
Message headers will introduce some basic headers from the JMS specification.
Every message will include the following headers:

#### JMSDestination
```json
{
  "JMSDestination": "topic:a08e91a0-3a3b-432f-a2a6-fd1bf2350583"
}
```
The destination that the message has been sent to.  The representation of a
destination is as a string `"{destination-type}:{destination-id}"`.

#### JMSMessageID
```json
{
  "JMSMessageID": "a08e91a0-3a3b-432f-a2a6-fd1bf2350583"
}
```
A unique ID to identify the specific message.  It always has a prefix of
"ID:", with uniqueness of the ID relying upon `UUID.randomUUID()`.

Message producers will be able to choose not to use this header via
the `setDisableMessageID` method.

#### JMSTimestamp
```json
{
  "JMSTimestamp": 15643200
}
```
The time, in the standard Java millis time value that the message was
succesfully sent to the provider.   

### Message Properties
```json
{
  "name": "property-name",
  "type": "String",
  "value": "x"
}
```
#### JMSXUserID
The identity of the user sending the message.

#### JMSXAppID
The identity of the application sending the message.

### Message Bodies
```json
{
  "type": "StreamMessage",
  "value": "XXX"
}
```
+   StreamMessage
+   MapMessage
+   TextMessage
+   ObjectMessage
+   BytesMessage