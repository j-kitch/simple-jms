# SimpleJMS 0.3.0

This release will build upon 0.2.0 by adding
+   Multiple topics.
+   Multiple queues.
+   Lifetimes for Producers and Consumers.

SimpleJMS will now require an API of
+   DestinationType - the type of destination
+   Destination - a unique identifier for a destination.
+   ProducerId - a unique identifier for a producer.
+   ConsumerId - a unique identifier for a consumer.
+   Message - a message received over SimpleJMS.
+   Session - an active client session.
+   Producer - a client's message producer.
+   Consumer - a client's message consumer.
+   Broker - a SimpleJMS provider broker.

## DestinationType
An enum of `QUEUE` and `TOPIC`.  

*This used to be `Destination`, but
has since been superseded with the requirement for multiple topics and queues.*

## Destination
A POJO unique identifier for a messaging destination, consisting of
+   `DestinationType getType()`
+   `UUID getId()`

## ProducerId
A POJO unique identifier for a message producer, consisting of
+   `Destination getDestination()`

    Get the destination that this producer is associated with
    
+   `UUID getId()`

    Get the unique ID of this producer.
    
The same UUID may be shared between a Topic, Queue, ProducerId and ConsumerId.

## ConsumerId
A POJO unique identifier for a message consumer, consisting of
+   `Destination getDestination()`

    The destination that this consumer is associated with.
    
+   `UUID getId()`

    The unique ID of this consumer.

## Message
A POJO describing a single message.  This POJO exposes the following methods

+   `String getMessage()`

    Retrieve the plain text message.
    
+   `Destination getDestination()`

    The destination that the message has been received from.
    
+   `ProducerId getProducerId()`

    The producer that the message was sent from.
    
## Session
A session
+   `Session(String brokerUrl, RestTemplate restTemplate)`
    
    Construct a new session connecting to the broker at the given url.

+   `Destination createDestination(DestinationType type)`

    Create a new destination of the specified type, requiring the broker
    to construct it's destination, and then return the information about
    the destination to the session.
    
+   `Consumer createConsumer(Destination destination)`

    Create a new consumer for the given destination, calling the broker,
    allowing the broker to create the resources for this consumer and then
    returning the consumer's information from the broker.
    
    Upon returning from this method, the consumer is active. 

+   `Producer createProducer(Destination destination)`

    Create a new producer for the given destination, calling the broker,
    allowing the broker to create the resources for this consumer and then
    returning the producer's information from the broker.
    
    Upon returning from this method, the producer is active.
    
## Producer
A producer has the following methods
+   `ProducerId getId()`

    Get the ID of this producer.

+   `void sendMessage(String message)`

    Send a message to the producer's destination.
    
+   `void close()`

    Close the producer, alerting the Broker that this producer
    is no longer active.
    
## Consumer
+   `ConsumerId getId()`

    Get the ID of this consumer.
    
+   `Optional<Message> receiveMessage()`

    Receive the next message for this consumer, or `Optional.empty()` if none.
    
+   `void close()`

    Close the consumer, alerting the Broker that this consumer is no
    longer active.
    
## Broker
The broker class will be a Spring Boot application exposing a REST API for communication.

It will provide the ability to create and use topics and queues that will exist for the
lifetime of the broker.

Consumers and Producers can be constructed to connect to these destinations.

**NOTE:** As of this version, no multi-threading safety is implemented,
synchronization must be implemented between consumers.

The broker must maintain the state of active consumers, in order to implement the
topic/queue semantics.

The broker will expose the following REST API

### Create a destination
This endpoint allows sessions to construct new destinations in the broker.

**Method:** `POST`

**URL:** `/{destination-type}`

Where `{destination-type}` is either
+   `topic` - construct a new topic
+   `queue` - construct a new queue

A successful response will return `200` and the body
```json
{
  "id": "{destination-id}"
}
```
Where `{destination-id}` is a UUID identifying the destination with `{destination-type}`.

### Create a consumer
This endpoint allows clients to create consumers for the broker's destinations.

**Method:** `POST`

**URL:** `/{destination-type}/{destination-id}/consumer` 

Where `{destination-type}` and `{destination-id}` are the type and id of the
destination we wish to create a consumer for.

A successful response will return `200` and the body 
```json
{
  "id": "{consumer-id}"
}
```
where `{consumer-id}` is a UUID identifying this consumer.

After this call, the unique id can be used to receive messages associated with this consumer from
either it's topic or queue.

A consumer ID is unique to it's topic or queue, and cannot be used across destinations.

### Create a producer
This endpoint allows clients to create producers for the broker's destinations.

**Method:** `POST`

**URL:** `/{destination-type}/{destination-id}/producer`

Where `{destination-type}` and `{destination-id}` are the type and id of the
destination we wish to create a consumer for.

A successful response will return `200` and the body
```json
{
  "id": "{producer-id}"
}
```
where `{producer-id}` is a UUID identifying this producer.

### Send a message
This endpoint allows producers to send messages to the topic or queue.

**Method:** `POST`

**URL:** `/{destination-type}/{destination-id}/producer/{producer-id}/send`

**Body:** 
```json
{
  "message": "{message}"
}
```

Where `{destination-type}` and `{destination-id}` are the type and id of the destination we wish
to send a message to,
and `{producer-id}` is the unique ID of the producer sending this message,
and where `{message}` is the plain text message to be sent to the destination.

A successful response will return `200` and an empty body.

After this call, the message will be appended to the end of the destination, and consumers will be
able to receive this message.

### Receive a message
This endpoint allows consumers to receive a message from their related topic/queue.

**Method:** `POST`

**URL:** `/{destination-type}/{destination-id}/consumer/{consumer-id}/receive`

Where `{destination-type}` and `{destination-id}` are the type and id of the destination we wish
to send a message to,
and `{consumer-id}` is the unique ID of the consumer receiving this message.

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
    
### Remove a producer
This endpoint allows producers to end their lifetime's connection to the broker.

**Method:** `DELETE`

**URL:** `/{destination-type}/{destination-id}/producer/{producer-id}`

Where `{destination-type}` and `{destination-id}` are the type and id of the destination we wish
to send a message to,

and `{producer-id}` is the id of the producer we wish to delete.

A successful response will return `200` and an empty body.

### Remove a consumer
This endpoint allows consumers to end their lifetime's connection to the broker.

**Method:** `DELETE`

**URL:** `/{destination-type}/{destination-id}/consumer/{consumer-id}`

Where `{destination-type}` and `{destination-id}` are the type and id of the destination we wish
to send a message to,

and `{consumer-id}` is the id of the consumer we wish to delete.

A successful response will return `200` and an empty body. 