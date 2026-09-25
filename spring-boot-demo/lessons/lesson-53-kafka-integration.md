# Lesson 53: Kafka Integration

## Questions

1. What is Apache Kafka?
2. What is a topic?
3. What is a partition?
4. What is an offset?
5. What is a producer?
6. What is a consumer?
7. What is a consumer group?
8. Where does kafka guarantee ordering?
9. Why should an event key often be based on an aggregate ID?
10. What does KafkaTemplate do?
11. What does @KafkaListener do?
12. Why can a Kafka consumer process the same record more than once?
13. What does at-least-once processing mean?
14. Why should consumers be idempotent?
15. What is a dead-letter topic?
16. What is the difference between retryable and non-retryable failures?
17. Why should event schemas evolve backward-compatibly?
18. What are kafka headers useful for?
19. Why is a database transaction not automatically atomic with a Kafka publish?
20. Why should exactly once claims be made carefully?

## My summary

- Kafka is an event-streaming platform
- Records are written to topics, topics are divided into partitions, and each record receives an offset within its partition.
- Kafka preserves ordering within a partition, not across all partitions in a topic
- Why Kafka?
  - Synchronous communication
    - Order Service
      - calls Payment Service
      - calls Inventory Service
      - calls Notification Service
    - the order request depends directly on every downstream service
  - Kafka-based communication:
    - Order Service
      - publishes OrderCreatedEvent :
        - payment Service
        - Inventory Service
        - Notification Service
    - the producer does not need to call every consumer directly
  - Kafka is useful for : 
    - Event-driven architecture
    - Asynchronous processing
    - Service decoupling
    - Audit histories
    - Data pipelines
    - Integration between bounded contexts
    - Replying historical events
- Kafka retains records according to topic retention settings
  - Consuming a record does not normally delete it immediately, consumers track their positions through offsets.
- Core KafKa concepts
  - Topic
    - A named stream of records:
      - orders
      - payments
      - notifications
  - Partition
    - A topic is divided into partitions for scalability and parallel processing,
    - orders
      - partition 0
      - partition 1
      - partition 2
  - Offset
    - A record's sequential position within a partition
    - partition 0
      - offset 0
      - offset 1
      - offset 2
  - Producer
    - publishes records to kafka
  - Consumer
    - reads records form kafka
  - Consumer Group
    - A group of consumers that cooperatively processes a topic
    - within one consumer group
      - each partition is assigned to at most one consumer at a time
      - different consumer groups receive their own copy of the topic's records logically
- Partitions and ordering
  - Kafka guarantees ordering withing one partition
    - partition 0:
      - OrderCreated-1
      - OrderPaid-1
      - OrderShipped-1
  - it does not guarantee global ordering across partitions
    - partition 0 : A1, A2, A3
    - partition 1 : B1, B2, B3
  - if order matters for a particular entity, use a stable key
  - records with same key are normally routed to the same partition, preserving their relative order there
- Spring Boot manages a compatible Kafka client and Spring Kafka version when the dependency is used through Boot dependency management
- Important settings : 
  - bootstrap-servers
    - Kafka broker addresses
  - group-id
    - Consumer group identity
  - auto-offset-reset
    - Behavior when no committed offset exists
  - Key/value serializer
    - Converts Java values to bytes
  - key/value deserializer
    - converts bytes back to Java values
- A good event should contain:
  - Event-specific data
  - A stable identifier
  - Event timestamp
  - Enough information for consumers
  - No database-specific implementation details
- Entities contain persistence behavior and relationships that should not become part of you messaging contract.
- KafkaTemplate is Spring Kafka's abstraction for sending records 
  - Spring auto-configures template when kafka producer configuration is available
  - Kafka sends are asynchronous
  - Do not assume that calling send() alone proves that the record was durably accepted.
- @KafkaListener creates a listener endpoint that consumes records from the configured topic and involves the annotated method
  - Spring Kafka provides listener containers to manage polling and delivery
- If payment and notification consumers used the same group id, kafka would load-balance records bbetween them instead of delivering every record to both logical subscribers
- A group cannot achieve more active partition-level parallelism than the topic has partitions
  - Consumer A -> partition 0
  - Consumer B -> partition 1
  - Consumer C -> partition 2
  - Consumer D -> idle
  - Consumer E -> idle
- By default, Spring Kafka manages offset commits according to the listener container's acknowledgment mode.
- Manual acknowledgement requires corresponding container configuration and should be used only when the offset should be commited after a clearly defined processing point.
  - if the process crashes before the offset commit, the record may be delivered again
  - Therefore, kafka consumers should usually be designed to tolerate duplicate delivery
- At least once processing
  - A common delivery model is at least once :
    - Record is delivered
    - Consumer processes it
    - Application crashes before offset commit
    - Record is delivered again
  - This means consumers must be idempotent
  - the idempotency strategy must be designed carefully, especially when external side effects are involved
- Consumer error handling
  - Do not allow an exception policy to become accidental behavior, decide : 
    - Retry?
    - Skip?
    - Pause?
    - Dead-letter?
    - Alert?
    - Manual intervention?
  - Spring kafka supports listener error handlers, backoff policies and dead-letter publishing recoverers
    - temporary network failure -> retry
    - invalid event schema -> dead letter
    - business rejection -> possibly no retry
- DLT
  - a Dead Letter Topic, stores records that could not be processed successfully
    - orders
    - consumer fails
    - orders.DLT
  - A DLT is not a place to hide failures permanently
  - build operational procedures for : 
    - inspecting failed records
    - correcting data
    - Replaying records
    - Tracking attempts
    - alerting operators
    - preventing repeated poison messages
- Kafka headers
  - headers can carry
    - Event type
    - Event version
    - correlation ID
    - trace ID
    - tenant ID
  - Do not use headers as a substitute for required business data.


```text
Kafka stores records in partitioned topics.
Producers publish records, consumers read them through consumer groups, and offsets track consumption progress.
Spring Boot provides KafkaTemplate for publishing and KafkaListener for consuming
Because records may be redelivered, consumers should be idempotent,
failures should have explicit retry and dead-letter policies, and database  plus kafka consistency requires an
architecture such as the transactional outbox pattern
```