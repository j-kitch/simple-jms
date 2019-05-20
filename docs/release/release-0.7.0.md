# SimpleJMS 0.7.0

This release will focus on adding message headers.

## Message Headers
We will introduce the following headers into this release:
+   *JMSDestination*

    A *Destination* containing the destination this message belongs to.
    
    Set by the provider upon receiving a message sent by a producer.

+   *JMSMessageID*

    A *String* with the format `ID:<UUID>`, where the UUID uniquely identifies
    the message as part of this provider.
    
    Set by the provider upon receiving a message sent by a producer.