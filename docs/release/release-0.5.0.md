# SimpleJMS 0.5.0

This release will focus on adding properties to messages.

## Properties
Messages will include properties, exposing all the property specific methods
of the JMS Message class.

Properties will **not** implement the exact semantics of the JMS specification,
but will serve as a rough base for future work to reach this point.

Properties will have no concept of read/write exclusivity, properties are
mutable when sending and receiving, with the properties used at send being
those received by consumers.