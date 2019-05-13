Feature: Message Properties

  Scenario: Message Properties are received
    Given a queue with a producer
    And a consumer is created
    When the producer sends messages with properties
    Then the consumer receives messages with properties