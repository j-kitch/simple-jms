# Issues 0.3.0

## General Code
+   No documentation/Javadoc.
+   No thread safety.
+   No error handling.
+   No authentication.
+   No validation.
+   Poor use of Jackson.
+   Relying on `UUID.randomUUID()` for UUIDs.
+   Value types equals/hashCode?

## Testing
+   Spring REST Docs for API documentation?
+   Less integration tests, more component tests - aim for testing pyramid.
+   Cucumber for integration tests?
+   Need jacoco.

## REST API
+   Poor documentation
+   Improper use of return statuses
+   No error handling
+   No authentication

## Broker
+   Spring Boot Application doesn't allow easy integration in other code.
+   No code-based control of startup/shutdown.

## DestinationService
+   Multiple maps per type rather than one map with Destination key.

## TopicService
+   Duplicating messages per consumer.
+   Linked-Lists are BAD.

## QueueService
+   Linked-Lists are BAD.

## Session
+   Requires passing `RestTemplate` instance to constructor.
