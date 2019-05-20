# SimpleJMS 0.8.0 Rest API

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
POST /producer
```
Create a producer for a specific destination.

### Request

#### Request Body

```json
{
  "destination": "{type}:{id}"
}
```
Path | Type | Optional | Description
---|---|---|---
$.destination | string | false | The destination which the producer wishes to connect to.

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
POST /consumer
```
Create a Consumer for the specific destination.

### Request

#### Request Body

```json
{
  "destination": "{type}:{id}"
}
```
Path | Type | Optional | Description
---|---|---|---
$.destination | string | false | The destination which the consumer wishes to connect to.

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
DELETE /producer/{id}
```
Delete a specific producer from the broker.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
id | string | false | The ID of the Producer to delete.

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
DELETE /consumer/{id}
```
Delete a specific consumer from a broker.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
id | string | false | The ID of the Consumer to delete.

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
POST /producer/{id}/send
```
Send a message to the broker using the specified Producer.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
id | string | false | The ID of the Producer.
    
#### Request Body
```json
{
  "headers": {
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
POST /consumer/{id}/receive
```
Receive a message for the specific consumer.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
id | string | false | The ID of the Consumer.

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

## Acknowledge a Message
```
POST /consumer/{id}/acknowledge
```
Acknowledge the receipt of all messages before and including a specified message.

### Request

#### Path Parameters
Parameter | Type | Optional | Description
---|---|---|---
id | string | false | The ID of the Consumer.

#### Request Body
```json
{
  "JMSMessageID": "{id}"
}
```

Path | Type | Optional | Description
---|---|---|---
$.JMSMessageID | string | false | The JMSMessageID of the acknowledged message.

### Response

#### 200: Ok
The message has been acknowledged.

#### 400: Bad Request
The broker failed to acknowledge the message.
```json
{
  "message": "{error-message}"
}
```
Path | Type | Optional | Description
---|---|---|---
$.message | string | false | A descriptive error message describing the reason why the message could not be acknowledged.
