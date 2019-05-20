# SimpleJMS 0.7.0 Rest API

## Create a Destination
```
POST /{destination-type}
```
Create a new destination to send/receive messages from.

### Request

#### Path Parameters

Parameter | Type | Optional | Description
---|---|---|---
destination-type | string | false | The type of destination to be created, a choice of either `queue` or `topic`.

### Response

#### 200: Ok
The destination was successfully created.
```json
{
  "id": "{destination-id}"
}
```

Path | Type | Optional | Description
---|---|---|---
$.id | string | false |  The ID of the created destination.  

## Create a Producer
```
POST /{destination-type}/{destination-id}/producer
```
Create a producer for a specific destination.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
destination-type | string | false |The type of the destination to create a producer for.
destination-id | string | false | The ID of the destination to create a producer for.

### Response

#### 200: Ok
The Producer was successfully created.

```json
{
  "id": "{producer-id}"
}
```
Path | Type | Optional | Description
---|---|---|---
$.id | string | false | The ID of the created producer.
    
#### 400: Bad Request
The broker failed to create the Producer.
```json
{
  "message": "{error-message}"
}
```
Path | Type | Optional | Description
---|---|---|---
$.message | string | false | A descriptive error message describing the reason why the producer failed to be created.

## Create a Consumer
```
POST /{destination-type}/{destination-id}/consumer
```
Create a Consumer for the specific destination.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
destination-type | string | false | The type of the destination to create a Consumer for.
destination-id | string | false | The ID of the destination to create a Consumer for.

### Response

#### 200: Ok
The Consumer was successfully created.

```json
{
  "id": "{consumer-id}"
}
```
Path | Type | Optional | Description
---|---|---|---
$.id | string | false | THe ID of the created Consumer.

#### 400: Bad Request
The broker failed to create the Consumer.
```json
{
  "message": "{error-message}"
}
```
Path | Type | Optional | Description
---|---|---|---
$.message | string | false | A descriptive error message describing the reason why the Consumer failed to be created.

## Delete a Producer
```
DELETE /{destination-type}/{destination-id}/producer/{producer-id}
```
Delete a specific producer from the broker.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
destination-type | string | false | The type of the destination of the producer.
destination-id | string | false | The ID of the destination of the Producer.
producer-id | string | false | The ID of the Producer to delete.

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
Path | Type | Optional | Description
---|---|---|---
$.message | string | false | A descriptive error message describing the reason why the Producer could not be deleted.


## Delete a Consumer
```
DELETE /{destination-type}/{destination-id}/consumer/{consumer-id}
```
Delete a specific consumer from a broker.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
destination-type | string | false | The type of the destination of the Consumer.
destination-id | string | false | The ID of the destination of the Consumer.
consumer-id | string | false | The ID of the Consumer to delete.

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
Path | Type | Optional | Description
---|---|---|---
$.message | string | false | A descriptive error message describing the reason why the Consumer could not be deleted.

## Send a Message
```
POST /{destination-type}/{destination-id}/producer/{producer-id}/send
```
Send a message to the broker using the specified Producer.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
destination-type | string | false | The type of the destination of the Producer.
destination-id | string | false | The ID of the destination of the Producer.
producer-id | string | false | The ID of the Producer.
    
#### Request Body
```json
{
  "headers": {
    "JMSDestination": null,
    "JMSMessageID": null
  },
  "properties": [
    {
      "name": "{property-name}", 
      "type": "{property-type}", 
      "value": "{property-value}"
    }
  ],
  "body": {
    "type": "{body-type}",
    "text": "{body-text}",
    "object": "{body-object}"
  }
}
```
Path | Type | Optional | Description
---|---|---|---
$.headers | object | false | The JMS message headers of this message.
$.headers.JMSDestination | null | false | The destination of this message, not set by the client.
$.headers.JMSMessageID | null | false | The message ID of this message, not set by the client.
$.body.type | string | false | The type of the message body, either *text* or *object*.
$.body.text | string | true | Present if the body is a text body, the text in the body.
$.body.object | string | true | Present if the body is an object body, the object in the body.
$.properties[] | array | false | The array of properties in the message.
$.properties[].name | string | false | The name of the property.
$.properties[].type | string | false | The type of the property.
$.properties[].value | bool, string, number | false | The value of the property.

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
Path | Type | Optional | Description
---|---|---|---
$.message | string | false | A descriptive error message describing the reason why the Producer could not send the message to the broker.

## Receive a Message
```
POST /{destination-type}/{destination-id}/consumer/{consumer-id}/receive
```
Receive a message for the specific consumer.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
destination-type | string | false | The type of the destination of the Consumer.
destination-id | string | false | The ID of the destination of the Consumer.
consumer-id | string | false | The ID of the Consumer.

### Response

#### 200: Ok
The Consumer has received it's message from the broker.

```json
{
  "headers": {
    "JMSDestination": "{destination-type}:{destination-id}",
    "JMSMessageID": "ID:{message-id}"
  },
  "properties": [
    {
      "name": "{property-name}", 
      "type": "{property-type}", 
      "value": "{property-value}"
    }
  ],
  "body": {
    "type": "{body-type}",
    "text": "{body-text}",
    "object": "{body-object}"
  }
}
```
Path | Type | Optional | Description
---|---|---|---
$.headers | object | false | The JMS message headers of this message.
$.headers.JMSDestination | string | false | The destination of this message, set by the broker.
$.headers.JMSMessageID | string | false | The message ID of this message, set by the broker.
$.body | object | true | Present if a message exists, the body of the message.
$.body.type | string | false | The type of the message body, either *text* or *object*.
$.body.text | string | true | Present if the body is a text body, the text in the body.
$.body.object | string | true | Present if the body is an object body, the object in the body.
$.properties[] | array | false | The array of properties in the message.
$.properties[].name | string | false | The name of the property.
$.properties[].type | string | false | The type of the property.
$.properties[].value | bool, string, number | false | The value of the property.

#### 400: Bad Request
The broker failed to send the next message to the consumer.

```json
{
  "message": "{error-message}"
}
```
Path | Type | Optional | Description
---|---|---|---
$.message | string | false | A descriptive error message describing the reason why the next message could not be sent to the Consumer.