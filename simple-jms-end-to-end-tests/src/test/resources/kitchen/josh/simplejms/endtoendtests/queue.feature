Feature: Queue

  Scenario: Only one consumer receives each message
    Given a queue with a producer and multiple consumers
    When the producer sends messages
    Then each message is only received by a single consumer

  Scenario: Messages are saved irrespective of consumers
    Given a queue with a producer
    And the producer sends messages
    When a consumer is created
    Then the consumer receives messages