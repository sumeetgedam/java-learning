# Lesson 54: Testing Spring Kafka

## Questions

1. Why are kafka integration tests needed in addition to unit tests?
2. What does @EmbeddedKafka do?
3. What is KafkaTestUtils?
4. Why should kafka tests often use unique topics?
5. Why should kafka tests often use unique consumer groups?
6. Why is a fixed Thread.sleep() usually a poor synchronization strategy?
7. What is the difference between embedded kafka and testcontainers kafka?
8. How can a test verify JSON serialization?
9. How can a test verify consumer-group behavior?
10. How can a test verify retry behavior?
11. How can a test verify dead-letter publishing?
12. Why should consumers be tested for duplicate delivery?
13. Why is a database uniqueness constraint useful for idempotency?
14. What does auto.offset.rest affect in tests?
15. Why can Spring context caching affect in tests?
16. What is the difference between testing a producer and testing a listener?
17. Ehy should Kafka tests use bounded waits?
18. What Kafaka behavior should be tested than mocked?

## My summary

- Spring kafka provides test utilities, embedded broker support, and helpers for producing and consuming records.
- Spring Boot protects can use Kafka test starter or spring-kafka-test, depending on the project's Boot version and dependency management
- Why test Kafka integration
  - unit tests can verify event-handling logic
  - but they do not prove that
    - the producer serializes the event correctly
    - the topic name is correct
    - the consumer deserializes the event correctly
    - the listener receives the message
    - the consumer group is configured
    - retry handling mode
    - failed records reach the DLT
  - Kafka integration tests verify the complete flow : 
    - Producer
    - Kafka broker
    - Topic
    - Consumer group
    - Listener
    - Application behavior
- Testing strategies
  - Unit test
    - no kafka broker
      - fast
      - test business logic
      - mocks KafkaTemplate or listener dependencies
  - Embedded Kafka
    - Starts a kafka protocol
    - No external Docker dependency
    - Good for focused integration tests
  - Testcontainers Kafka
    - Starts kafka in a container : 
      - Closer to deployment infrastructure
      - Requires Docker
      - Useful for realistic integration tests
  - Full environment test
    - Runs the application and kafka together
      - Most realistic
      - slowest
      - useful for critical workflows
- Embedded Kafka
  - Spring kafka provides `@EmbeddedKafka` for starting an embedded broker during tests
  - Kafka 4 uses KRaft 
- KafkaTestUtils provides helpers for consumer properties, producer properties, polling records and retrieving a single expected record
- Tests can interfere with each other if they reuse :
  - same topic
  - same consumer group
  - same embedded broker
- Spring kafka supports exception classification so that selected exceptions can be retried.
- Embedded Kafka vs testcontainers

| Concern                 | Embedded Kafka            | Test containers            |
|-------------------------|---------------------------|----------------------------|
| Startup speed           | Usually faster            | Usually slower             | 
| Docker required         | No                        | Yes                        | 
| Real broker environment | Good                      | More realistic             | 
| CI setup                | simpler                   | Requires container runtime | 
| Broker configuration    | more limited              | Flexible                   | 
| Best use                | Focused kafka integration | infrastructure level tests |


```text
Kafka tests should verify the complete producer-broker-consumer path, not only mocked method calls.
Embedded Kafka is useful for fast integration tests, while Testcontainers provides a more realistic broker environment.
Reliable tests use unique topics or groups, bounded asynchronous waits, explicit offset behavior,
retry adn DLT assertions and idempotency checks
```