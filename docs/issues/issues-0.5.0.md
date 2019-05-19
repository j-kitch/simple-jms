# Issues 0.5.0

## General Code
+   No thread safety.
+   No authentication.
+   No validation.
+   Poor use of Jackson.
+   Relying on `UUID.randomUUID()` for UUIDs.
+   Value types equals/hashCode?

## REST API
+   No authentication
+   Destination specific producer/consumers might not be a good idea.

## Broker
+   Spring Boot Application doesn't allow easy integration in other code.
+   No code-based control of startup/shutdown.

## TopicService
+   Duplicating messages per consumer.
+   Linked-Lists are BAD.

## QueueService
+   Linked-Lists are BAD.

## Session
+   Requires passing `RestTemplate` instance to constructor.
