# SimpleJMS 0.2.0

This version will build upon 0.1.0 by adding to the subset of basic functionality to allow us to
>   send and receive plain-text messages from a non-persistent queue via REST.

This version should provide an API with the following classes
+   Broker - a messaging broker for the destinations
+   Session - a session associated with the broker for creating consumers and producers
+   Consumer - a unique consumer associated with either the topic or queue
+   Producer - a producer associated with either the topic or queue
+   Message - a plain text message associated with a topic or queue
+   Destination - a representation of the destinations available in the broker

## Broker
The broker class will be a Spring Boot application exposing a REST API for communication.

It will provide the following destinations
+   `topic`

    A single, non-persistent topic for the lifetime of the broker.
    
    Consumers are associated with the topic upon consumer construction,
    the broker will start maintaining pending messages for a consumer
    upon consumer construction.
    
+   `queue`

    A single, non-persistent queue for the lifetime of the broker.

    Consumers are associated with the queue upon consumer construction,
    consumers share the single global queue, having access to all previous
    messages before consumer construction, but only one consumer receives each
    message.

    **NOTE:** As of this version, no multi-threading safety is implemented,
    synchronization must be implemented between consumers.

The broker must maintain the state of active consumers, in order to implement the
topic/queue semantics.

The broker will expose the following REST API

### Create a consumer
This endpoint allows clients to create consumers for the broker's destinations.

**Method:** `POST`

**URL:** `/{destination}/consumer` 

Where `{destination}` is one of the possible destinations of
+   `topic` - the unique topic in this broker
+   `queue` - the unique queue in this broker

A successful response will return `200` and the body 
```json
{
  "id": "{unique-id}"
}
```
where `{unique-id}` is a UUID identifying this consumer.

After this call, the unique id can be used to receive messages associated with this consumer from
either it's topic or queue.

A consumer ID is unique to it's topic or queue, and cannot be used across destinations.
    
### Send a message
This endpoint allows producers to send messages to the topic or queue.

**Method:** `POST`

**URL:** `/{destination}/send`

**Body:** 
```json
{
  "message": "{message}"
}
```

Where `{destination}` is a choice of either
+   `topic` - the unique topic in this broker
+   `queue` - the unique queue in this broker

and where `{message}` is the plain text message to be sent to `{destination}`.

A successful response will return `200` and an empty body.

After this call, the message will be appended to the end of the destination, and consumers will be
able to receive this message.

### Receive a message
This endpoint allows consumers to receive a message from their related topic/queue.

**Method:** `POST`

**URL:** `/{destination}/receive/{id}`

Where `{destination}` is either
+   `topic` - the unique topic in this broker
+   `queue` - the unique queue in this broker

and `{id}` is the unique id of this consumer.

A successful response will return `200` and

+   if the consumer has a message to receive, it will return

    ```json
    {
      "message": "{message}"
    }
    ```
    
    where `{message}` is the plain text message received from the destination.

+   if the consumer doesn't have a message to receive, it will return

    ```json
    {
      "message": null
    }
    ```

## Session
A session will be a client class associated with a single broker instance.
It will expose the following methods

+   `Consumer createConsumer(Destination destination)`

    Create a new consumer for the given destination, notifying the broker of the consumer
    and returning the new consumer instance.
    
+   `Producer createProducer(Destination destination)`

    Create a new producer for the given destination, returning a producer instance.
    
## Consumer
A consumer class for receiving messages from a destination.  The consumer class exposes the following methods

+   `Optional<Message> receiveMessage()`

    If a new message exists, this will return the message as a String, or if
    no further messages exist currently, will return `Optional.empty()`.

+   `Destination getDestination()`

    Return the destination that this consumer is associated with.
    
## Producer
A producer class for sending messages to a destination, it exposes the following methods

+   `void sendMessage(String message)`

    Which sends the plain text message to the destination, allowing consumers to then
    receive this message.
    
+   `Destination getDestination()`

    Return the destination that this consumer is associated with.
    
## Message
A message instance contains a plain text message, available via the following method

+   `String getMessage()`

    Access the plain text message in this topic's message.
    
+   `Destination getDestination()`

    The destination that the message has been received from.
    
## Destination
A destination in a broker, an enumeration of
```
TOPIC, QUEUE
```