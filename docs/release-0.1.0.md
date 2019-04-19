# SimpleJMS 0.1.0

This initial version should function as a very rough implementation of a basic subset of functionality.  We will begin
with 

> Sending and receiving plain text messages to a non-persistent topic via REST.

This version should provide an API with the following classes
+   Broker - a messaging broker for the topic
+   Session - a session associated with the broker for creating consumers and producers
+   Consumer - a message consumer associated with the topic
+   Producer - a message producer associated with the topic
+   Message - a plain text message associated with the topic
    
## Broker
The broker class will be a Spring Boot application exposing a REST API for communication.

It will provide a single, non-persistent topic for the lifetime of the broker, this single
topic is uniquely identified with the broker.

It will maintain internal state of which consumers are currently consuming from the topic,
storing messages for consumers until they receive them.

The broker will expose the following REST API

### Create Consumer
To create a new, active consumer for the topic,

Method | URL
---|---
POST | /consumer | 

A successful response will return `200` and the body 
```json
{
  "id": "{unique-id}"
}
```
where `{unique-id}` is a UUID identifying this consumer.  After this call, all messages
sent to the broker will be stored for this consumer.

### Send Message
Producers send messages to the topic using the following endpoint.

Method | URL
--- | ---
POST | /producer

With the following body

```json
{
  "message": "{message}"
}
```
Where `{message}` is the plain text message to be added to the broker.

A successful response will return `200` and an empty body.

### Receive Message
Consumers receive messages using this endpoint.

Method | URL
--- | ---
POST | /consumer/`{id}`

where `{id}` is the unique id of this consumer.

A successful response will return '200' and

if a message is waiting to be sent to the consumer, the consumer will receive

```json
{
  "message": "{message}"
}
```

where `{message}` is the plain text message sent to the consumer.

Or, if there isn't a message to be sent to the consumer, the consumer will receive

```json
{
  "message": null
}
```
    
## Session
A session will be a client class associated with a single broker instance.
It will expose the following methods

+   `Consumer createConsumer()`

    Create a new consumer for the topic, telling the broker of the new consumer
    and returning a Consumer instance.
    
+   `Producer createProducer()`

    Create a new producer for the topic, returning a producer instance.
    
## Consumer
A consumer class for receiving messages from a broker.  The consumer class exposes the following methods

+   `Optional<String> receiveMessage()`

    If a new message exists, this will return the message as a String, or if
    no further messages exist currently, will return `Optional.empty()`.
The consumer implementation should be

## Producer
A producer class for sending messages to a broker, it exposes the following methods

+   `void sendMessage(String message)`

    Which sends the plain text message to the broker, to be received by all
    current consumers.
    
        
## Message
A message instance contains a plain text message, available via the following method

+   `String getMessage()`

    Access the plain text message in this topic's message.