# SimpleJMS 0.6.0

This release will focus on adding another type of message body, paving the
way for handling multiple kinds of message bodies in the future.

## Message Bodies
Messages can now be either `TextMessage` or `ObjectMessage` instances.  Both
of these message types handle the same property functionality, but differ in
their bodies.
+   `TextMessage`
    
    The previous message type, with a plain `java.lang.String` body.
    
+   `ObjectMessage`

    A new message type, with a `java.lang.Serializable` serialized body.
    This body will be encoded as Base64 when sent via REST.
    
## Jackson
This release will also clean up the use of Jackson so far, removing mutable
models.