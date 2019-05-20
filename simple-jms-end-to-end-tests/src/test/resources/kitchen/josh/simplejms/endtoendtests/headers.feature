Feature: Message Headers

  Scenario: JMSDestination
    Given a topic with a producer and multiple consumers
    When the producer sends messages
    Then each consumer receives a message with the JMSDestination set

  Scenario: JMSMessageID
    Given a queue with a producer and multiple consumers
    When the producer sends messages
    Then each consumer receives a message with a unique JMSMessageID