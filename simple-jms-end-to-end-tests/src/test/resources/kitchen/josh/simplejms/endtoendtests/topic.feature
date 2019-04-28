Feature: Topic

  Scenario: Every consumer receives each message
    Given a topic with a producer and multiple consumers
    When the producer sends messages
    Then each message is received by every consumer

  Scenario: Messages are retained after a consumer is created
    Given a topic with a producer
    And the producer sends messages
    When a consumer is created
    Then the consumer receives no messages