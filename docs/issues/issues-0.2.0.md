# Issues 0.2.0

## Broker REST API
+   Poor documentation
+   Improper use of return statuses
+   No error handling
+   No authentication/authorization

## Broker
+   Spring Boot Application doesn't allow easy integration in other code.
+   No documentation
+   No code-based control of startup/shutdown.

## TopicController/QueueController
+   No documentation.
+   No multi-threading handling.
+   No error handling.
+   No authentication/authorization.
+   No validation.
+   Poor use of Jackson.
+   Accept messages from anyone calling producer endpoint.

## TopicService
+   No documentation.
+   Infinite-lifetime consumers.
+   Duplicating messages per consumer.
+   Relying on Java's `UUID.randomUUID()` for id uniqueness.
+   No multi-threading handling.
+   Linked-Lists are BAD.
+   Only handles a single topic.

## QueueService
+   No documentation
+   Infinite-lifetime consumers.
+   Relying on Java's `UUID.randomUUID()` for id uniqueness.
+   No multi-threading handling.
+   Linked-Lists are BAD.
+   Only handles a single queue.

## Session
+   No documentation.
+   Requires passing `RestTemplate` instance to constructor.
+   No verification of host existing upon creation.
+   No error handling.
+   No multi-threading handling.

## Producer
+   No documentation.
+   No concept of identity for producers.
+   No lifetime of producers.
+   No error handling.
+   No multi-threading handling.

## Consumer
+   No documentation.
+   No concept of lifetimes for consumers.
+   No error handling.
+   No multi-threading handling.