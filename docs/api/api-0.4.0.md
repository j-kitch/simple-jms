# Rest API

## Create a Destination
```
POST /{destination-type}
```
Create a new destination to send/receive messages from.

### Request

#### Path Parameters
+   destination-type

    **type**: string
    
    **optional**: false

    The type of destination to be created, a choice of either
    `queue` or `topic`.
    

### Response

#### 200: Ok
The destination was successfully created.
```json
{
  "id": "{destination-id}"
}
```

+   destination-id

    **type**: string
    
    **optional**: false
    
    The ID of the created destination.  Together the destination
    type and destination ID uniquely identify the destination.
    
## Create a Producer
```
POST /{destination-type}/{destination-id}/producer
```
Create a producer for a specific destination.

### Request

#### Path Parameters
+   destination-type

    **type**: string
    
    **optional**: false
    
    The type of the destination to create a producer for.
    
+   destination-id

    **type**: string
    
    **optional**: false
    
    The ID of the destination to create a producer for.

### Response

#### 200: Ok
The Producer was successfully created.

```json
{
  "id": "{producer-id}"
}
```
+   producer-id

    **type**: string
    
    **optional**: false
    
    The ID of the created producer.
    
#### 400: Bad Request
The broker failed to create the Producer.
```json
{
  "message": "{error-message}"
}
```
+   error-message

    **type**: string
    
    **optional**: false
    
    A descriptive error message describing the reason why the producer
    failed to be created.

## Create a Consumer
```
POST /{destination-type}/{destination-id}/consumer
```
Create a Consumer for the specific destination.

### Request

#### Path Parameters
+   destination-type

    **type**: string
    
    **optional**: false
    
    The type of the destination to create a Consumer for.
    
+   destination-id

    **type**: string
    
    **optional**: false
    
    The ID of the destination to create a Consumer for.

### Response

#### 200: Ok
The Consumer was successfully created.

```json
{
  "id": "{consumer-id}"
}
```
+   consumer-id

    **type**: string
    
    **optional**: false
    
    The ID of the created Consumer.
    
#### 400: Bad Request
The broker failed to create the Consumer.
```json
{
  "message": "{error-message}"
}
```
+   error-message

    **type**: string
    
    **optional**: false
    
    A descriptive error message describing the reason why the Consumer
    failed to be created.


## Delete a Producer
```
DELETE /{destination-type}/{destination-id}/producer/{producer-id}
```
Delete a specific producer from the broker.

### Request

#### Path Parameters
+   destination-type

    **type**: string
    
    **optional**: false
    
    The type of the destination of the producer.
    
+   destination-id

    **type**: string
    
    **optional**: false
    
    The ID of the destination of the Producer.
    
+   producer-id

    **type**: string
    
    **optional**: false
    
    The ID of the Producer to delete.

### Response

#### 200: Ok
The Producer was successfully deleted in the broker.

#### 400: Bad Request
The broker failed to delete the specified Producer.

```json
{
  "message": "{error-message}"
}
```
+   error-message

    **type**: string
    
    **optional**: false
    
    A descriptive error message describing the reason why the Producer
    could not be deleted.


## Delete a Consumer
```
DELETE /{destination-type}/{destination-id}/consumer/{consumer-id}
```
Delete a specific consumer from a broker.

### Request

#### Path Parameters
+   destination-type

    **type**: string
    
    **optional**: false
    
    The type of the destination of the Consumer.
    
+   destination-id

    **type**: string
    
    **optional**: false
    
    The ID of the destination of the Consumer.
    
+   consumer-id

    **type**: string
    
    **optional**: false
    
    The ID of the Consumer to delete.

### Response

#### 200: Ok
The Consumer was successfully deleted in the broker.

#### 400: Bad Request
The broker failed to delete the specified Consumer.

```json
{
  "message": "{error-message}"
}
```
+   error-message

    **type**: string
    
    **optional**: false
    
    A descriptive error message describing the reason why the Consumer
    could not be deleted.

## Send a Message
```
POST /{destination-type}/{destination-id}/producer/{producer-id}/send
```
Send a message to the broker using the specified Producer.

### Request

#### Path Parameters
+   destination-type

    **type**: string
    
    **optional**: false
    
    The type of the destination of the Producer.
    
+   destination-id

    **type**: string
    
    **optional**: false
    
    The ID of the destination of the Producer.
    
+   producer-id

    **type**: string
    
    **optional**: false
    
    The ID of the Producer.
    
#### Request Body
```json
{
  "message": "{message}"
}
```
+   message
    
    **type**: string
    
    **optional**: false
    
    The message to be sent via the producer.

### Response

#### 200: Ok
The Producer successfully sent the message to the broker.

#### 400: Bad Request
The broker failed to receive the message from the Producer.

```json
{
  "message": "{error-message}"
}
```
+   error-message

    **type**: string
    
    **optional**: false
    
    A descriptive error message describing the reason why the Producer
    could not send the message to the broker.

## Receive a Message
```
POST /{destination-type}/{destination-id}/consumer/{consumer-id}/receive
```
Receive a message for the specific consumer.

### Request

#### Path Parameters
+   destination-type

    **type**: string
    
    **optional**: false
    
    The type of the destination of the Consumer.
    
+   destination-id

    **type**: string
    
    **optional**: false
    
    The ID of the destination of the Consumer.
    
+   consumer-id

    **type**: string
    
    **optional**: false
    
    The ID of the Consumer.

### Response

#### 200: Ok
The Consumer has received it's message from the broker.

```json
{
  "message": "{message}"
}
```
+   message

    **type**: string
    
    **optional**: true
    
    The message the broker has sent to the consumer, or `null` if
    there isn't a message for the consumer to receive.

#### 400: Bad Request
The broker failed to send the next message to the consumer.

```json
{
  "message": "{error-message}"
}
```
+   error-message

    **type**: string
    
    **optional**: false
    
    A descriptive error message describing the reason why the 
    next message could not be sent to the Consumer.