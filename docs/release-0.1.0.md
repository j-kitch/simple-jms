# SimpleJMS 0.1.0

This initial version should function as a very rough implementation of basic functionality.  We will ignore the JMS API,
Queues, Connection Factories, Transactions, Redelivery ect for now and focus on simply being able to send messages via
topics.

This version should provide an API with the following classes
+   Broker
+   Session
+   Consumer
+   Producer
+   Message
    
## Broker
The broker class should
+   exposes itself over a port, with the URI acting as the broker URI.
+   provide a non-persistent topic for the lifetime of the broker instance.
+   listens for HTTP messages sent from producers and adds them to the topic in any order.
+   sends HTTP topic messages to all active consumers.
    
## Session
The session implementation should
+   accept a URI to locate a Broker
+   create Consumer and Producer instances for the Broker's single topic.
+   registers Consumers with the Broker instance upon Consumer creation.

## Consumer
The consumer implementation should be
+   a topic subscription consumer, it consumes messages from a Broker instance's topic.

## Producer
The producer implementation should be
+   a topic producer, it can only send messages to a Broker instance's topic.

## Message
A message should be
+   A plain text message.

Note that this version does not allow the deletion of consumers.  Failed HTTP messages should be logged and ignored.
The Broker only implements a single non-persistent topic, uniquely identified with the broker.
