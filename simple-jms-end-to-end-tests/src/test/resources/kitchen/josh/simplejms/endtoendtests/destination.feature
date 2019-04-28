Feature: Destinations

  Scenario: Messages cannot be sent and received from non-existent destinations
    Given a fake destination with a producer and consumer
    When the producer sends messages
    Then the consumer receives no messages

  Scenario: Each destination is unique
    Given multiple destinations, each with producers and consumers
    When each destination's producers send messages
    Then each destination's consumers only receive their destinations messages