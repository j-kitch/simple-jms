Feature: Destinations

  Scenario: Messages cannot be sent to a non-existent destination
    Given a fake destination with a producer and consumer
    When the producer tries to send a message
    Then an exception was thrown

  Scenario: Messages cannot be received from a non-existent destination
    Given a fake destination with a producer and consumer
    When the consumer tries to receive a message
    Then an exception was thrown

  Scenario: Each destination is unique
    Given multiple destinations, each with producers and consumers
    When each destination's producers send messages
    Then each destination's consumers only receive their destinations messages