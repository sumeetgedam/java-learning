# Lesson 64:  Sagas, Transaction Outbox, and Event-Driven Workflows

## Questions

1. Why are distributed transactions difficult?
2. What is two-phase commit?
3. What is a Saga?
4. What is a compensating action ?
5. What is the difference between a local rollback and a compensation ?
6. Compare choreography and orchestration
7. What is dual-write problem?
8. What is Transactional outbox pattern?
9. Why must business data and the outbox row be written in one local transaction?
10. Why can an outbox publisher still publish duplicates?
11. What is the inbox pattern?
12. Why are stable event IDs important?
13. What is the difference between a command and an event?
14. How should Saga Workflow state be modeled?
15. What happens if a compensation fails?
16. How can event ordering be preserved per aggregate?
17. Why does the outbox pattern generally provide at-least-once publication ?
18. How can consumers achieve effectively-once business behavior?
19. When is orchestration preferable?
20. When is choreography preferable?
21. What should be monitored in an outbox and Saga System?

## My summary

### The distributed transaction problem
- Suppose an order workflow uses three services :
    - Order service
    - Payment service
    - Inventory service
- Each service owns its own database : 
  - Order DB
  - Payment DB
  - Inventory DB
- A business operation may require : 
1. Create order
2. Reserve inventory
3. Authorize payment
4. Confirm order

- There is n ordinary local transaction spanning all three databases
- Possible failure :
  - Order Created
  - Inventory reserved
  - Payment authorization fails
- The system must recover : 
  - Release Inventory
  - Cancel order
- This is a distributed workflow, not a single database transaction


### Two-phase commit
- Two-phase commit, or 2PC, coordinates multiple participants 
  - Coordinator
    - Order DB
    - Payment DB
    - Inventory DB
- Phase 1 : Prepare
  - Coordinator ---> Participants :
    - Can you commit ?
  - Participants : 
    - Reserve resources
    - Reply prepared or failed
- Phase 2 : Commit
  - If all prepared : 
    - Coordinator ---> commit
  - Otherwise : 
    - Coordinator ---> rollback
- Advantages : 
  - Atomic cross-resource commit
- Disadvantages : 
  - Blocking coordinator
  - Long-held locks
  - Higher latency
  - Availability problems
  - Operational complexity
  - Poor fit for many independent services
- For most microservice business workflows, prefer local transactions plus a Daga rather than attempting one global transaction


### What is a Saga ?
- A Saga is a response of local transactions
- Each service  :
1. Updates its own database
2. Commits locally
3. Publishes a message or response

- If a later step fails, earlier steps are undone through compensating actions
- Example : 
  - Create Order
  - Reserve inventory
  - Authorize Payment
  - Confirm order
- Compensation
  - Payment fails
  - Release inventory
  - Cancel order
- A compensation is not a database rollback.
- It is a new business operation that reverses or beutralizes the previous effect


### Local transaction vs compensation
- Suppose inventory is reserved : 
  - Inventory:
    - available = 0
    - reservation = action
- If payment fails, the compensation is : 
  - Release reservation
- This does not restore the exact historical database state in a technical sense
- It creates a new valid business state.
  - Before
    - inventory available = 1
  - After reserve:
    - inventory available = 0
    - reservation = active
  - After compensation
    - inventory available = 1
    - reservation = released
- The reservation history remains auditable

### Saga states
- A workflow should have explicit states
  - Order : 
    - PENDING
    - INVENTORY_RESERVED
    - PAYMENT_PENDING
    - CONFIRMED
    - CANCELLED
    - COMPENSATION_PENDING
    - FAILED
- Avoid relying only on distributed events without storing workflow state
- example :
  - order_id = 1001
  - state = PAYMENT_PENDING
- This allows the system to : 
  - Resume after restart
  - Retry failed steps
  - Display status to users
  - Investigate stuck workflow
  - Run reconciliation

### Choreography
- In choreography, services react to events without a central coordinator.
  - Order service
    - publishes OrderCreated
  - Inventory service
    - reserves inventory
    - publishes InventoryReserved
  - Payment service
    - authorizes payment
    - publishes PaymentAuthorized
  - Order serviec
    - confirms order
- Failure flow
  - PaymentFailed
  - Inventory service releases inventory
  - Order service cancels order
- Advantages : 
  - No central orchestrator
  - Services remain independently reactive
  - Simple for short workflows
- Disadvantages : 
  - Workflow is distributed across event handlers
  - Harder to understand end-to-end
  - Implicit coupling through event contract
  - Difficult failure visibility
  - Risk of event chains becoming tangled
- Choreography works well for simple workflows with few participants


### Orchestration
- In orchestration, a coordinator directs the workflow,
  - Order Saga Orchestrator
    - Reserve inventory
    - Authorize payment
    - Confirm order
- Example :
1. Orchestration sends ReserveInventory 
2. Inventory replies InventoryReserved
3. Orchestrator sends AuthorizePayment
4. Payment replies PaymentAuthorized
5. Orchestrator sends ConfirmOrder

- Failure
  - PaymentFailed
  - Orchestrator sends ReleaseInventory
  - Orchestrator sends CancelOrder

- Advantages  :
  - Explicit workflow
  - Centralized state
  - Clear failure handling
  - Easier operational visibility
- Disadvantages
  - Orchestrator becomes important infrastructure
  - Can accumulate business logic
  - Service must understand orchestration commands
- Orchestration is often better for complex or business-critical workflows

### Commands and Events in a Saga
- Use commands to request actions  :  
  - ReserveInventory
  - AuthorizePayment
  - ReleaseInventory
  - CancelOrder
- Use events to describe completed facts: 
  - InventoryReserved
  - PaymentAuthorized
  - PaymentFailed
  - InventoryReleased
  - OrderCancelled
- Conceptually : 
  - Command :
    - "Please do this."
  - Event  :
    - "This has happened."
- Do not treat an event as a guaranteed command to one service unless the contract explicitly says so.

### Transactional outbox pattern

- Consider
```java
@Transactional
public void createOrder(Order order) {
    orderRepository.save(order);
    kafkaTemplate.send(
            "orders",
            new OrderCreatedEvent(order)
    );
}
```
- The database transaction and kafka publish are separate operations.
- Possible outcomes : 
  - Database succeeds, kafka fails : 
    - Order exists
    - No OrderCreated event
    - Downstream services never process it
  - Kafka succeeds, database rolls back
    - Consumers receive an event
    - Order does not exist in the database
- This is the dual-write problem

### Transactional outbox pattern

- The transactional outbox pattern writes the business data and an outgoing event to the same database transaction.
  - One database transaction
    - Save order
    - Save outbox event
- Then a separate publisher sends the outbox events to kafka
  - Order DB
    - orders table
    - outbox_events table
      - Order Publisher
        - Kafka
- If the transaction commits : 
  - Order exits 
  - Outbox event exits
- If transaction rolls back : 
  - Neither exists
- This eliminates the database / kafka dual-write inconsistency at the source

### Outbox table 
- Example schema : 
```sql
CREATE TABLE outbox_events (
  id             UUID PRIMARY KEY,
  aggregate_type VARCHAR(100) NOT NULL,
  aggreagte_id   VARCHAR(100) NOT NULL,
  event_type     VARCHAR(200) NOT NULL,
  event_version  INTEGER NOT NULL,
  payload        JSON NOT NULL,
  created_at     TIMESTAMP NOT NULL,
  published_at   TIMESTAMP NULL,
  attempts       INTEGER NOT NULL DEFAULT 0,
  status         VARCHAR(30) NOT NULL
);
```
- Possible statuses  :
  - PENDING
  - PUBLISHING
  - PUBLISHED
  - FAILED
- Important fields : 
  - id
  - aggregate ID
  - event type
  - version
  - payload
  - created timestamp
  - publishing state
  - attempt count
- The event ID must remain stable across retries


### Writing business data and outbox event

- Conceptual service : 
```java
@Transactional
public Order createOrder(CreateOrderRequest request) {
    Order order = 
            orderRepository.save(
                    Order.pending(request)
            );
    OrderCreatedEvent event = 
            new OrderCreatedEvent(
                    order.getId(),
                    order.getCustomerId(),
                    order.getTotal()
            );
    
    outboxRepository.save(
            OutboxEvent.pending(
                    event.eventId(),
                    "Order",
                    order.getId().toString(),
                    "OrderCreated",
                    event
            )
    );
    return order;
}
```
- Both writes use the same local database transaction
- Guarantee : 
  - Order commited    -> outbox event commited
  - Order rolled back -> outbox event rolled back
- The publisher is responsible for delivery after the transaction commits.

### Outbox publisher
- A publisher periodically outbox pending events : 
1. Find pending outbox rows
2. Publish event to Kafka 
3. Receive send result
4. Mark event as published

- Flow : 
  - Outbox table
  - Publisher polls
  - Kafka publish
    - success
  - Mark PUBLISHED
- Pseudo-code :
```java
public void publishPendingEvents() {
    List<OutboxEvent> events =
            repository.findPendingBatch(100);
    
    for(OutboxEvent event : events) {
        try {
            KafkaTemplate.send(
                    event.topic(),
                    event.aggregateId(),
                    event.payload()
            );
            
            repository.markPublished(
                    event.id()
            );
            
        }catch(Exception expcetion) {
            repository.recordFailure(
                    event.id(),
                    exception.getMessage()
            );
        }
    }
} 
```
- This simplified example has race conditions and should be improved for production.

### Outbox publisher failure
- Suppose
1. Publisher sends event successfully
2. Publisher crashes before marking row published
3. Publishes restarts 
4. Same event is sent again

- The outbox pattern commonly provides : 
  - At-least-once event publication
- Therefore, consumers must be idempotent
- The outbox does not automatically provides exactly-once delivery to external consumers.


### Claiming outbox rows
- Multiple publisher instances may run concurrently
- Unsafe flow : 
  - Publisher A reads event 1
  - Publisher B reads event 1
  - Both publish event 1
- Duplicates may be acceptable if consumers are idempotent, but unnecessary duplication should still be reduced.
- Possible techniques : 
  - Row locking
  - FOR UPDATE SKIP LOCKED
  - Status transition with compare-and-set
  - Partition ownership
  - Leasing
  - Publisher sharding
- Conceptual state transition : 
```sql
UPDATE outbox_events
SET status = 'PUBLISHING'
WHERE id = ? AND status 'PENDING'; 
```
- Only the publisher that successfully updates the row owns the attempt
- Still, the event may be duplicated if the process crashes after publishing and before making it complete


### Polling vs CDC

#### Polling publisher
  - The application queries the outbox table periodically
    - SELECT pending rows
  - Advantages : 
    - Simple 
    - Easy to understand 
    - Uses ordinary database access
  - Disadvantages :
    - Polling overhead
    - Publication delay
    - Locking complexity
    - Database load
#### Change Data Capture
  - A CDC tool reads database log changes and publishes them
    - Database transaction log
    - CDC connector
    - Kafka
  - Advantages : 
    - low-latency change propagation
    - Less polling
    - Reads committed database changes
  - Disadvantages : 
    - Additional infrastructure 
    - Connector operations
    - Schema and deployment complexity
- Both approaches are valid
- The pattern matters more than the specific publisher mechanism

### Outbox event ordering
- Suppose an order produces : 
  - OrderCreated
  - OrderPaid
  - OrderShipped
- Consumer may require ordering
- Ordering challenges arise when : 
  - Multiple publisher instances
  - Multiple outbox workers
  - Multiple Kafka partitions
  - Retries
- Use a stable aggregate key :
  - Kafka key = orderId
  - This routes events for one order to the same partitino, preserving order within that positino
- However, publish timing can still create ordering issues if events are produced by separate workflows or retries
- Possible controls : 
  - Sequence number per aggregate
  - Outbox ordering by aggregate
  - One publisher per partition
  - Consumer version checks
  - State-transition validation

### Inbox pattern
- The inbox pattern received messages before processing them
  - Kafka event
  - Inbox table
  - Business transaction
- Flow : 
1. Receive events
2. Insert event ID into inbox
3. If duplicate, skip
4. Apply business changes
5. Commit inbox record and business change together


### Outbox plus Inbox
- A robust event-driven service may use both : 
  - Incoming event
  - Inbox / deduplication
  - Local business transaction
    - update local state
    - write outgoing outbox event
      - Publisher
      - Kafka
- This provides : 
  - Idempotent consumption
  - Reliable local state change
  - Reliable outgoing publication

### Event Driven order workflow
- Success Flow
  - Client
  - Order Service
    - save order PENDING
    - save OrderCreated outbox
      - Outbox Publisher
      - Kafka
        - Inventory Service
          - consume OrderCreated
          - reserve inventory
          - publish InventoryReserved
            - Kafka
              - Payment service
                - consumer InventoryReserved
                - authorize payment
                - publish PaymentAuthorized
                  - Kafka
                    - Order service
                      - mark order CONFIRMED

- Failure  :
  - PaymentFailed
  - Inventory service releases reservation
  - Order service marks order CANCELLED

### Compensation failure
- Compensating actions can also fail
- Example : 
  - Payment failed
  - Release inventory request fails
- Do not silently abandon the workflow
- Use : 
  - Compensation retry
  - Compensation pending state
  - Ded-letter handling
  - Operational alert
  - Reconciliation job
  - Manual recovery
- Example state : 
  - CANCELLATION_PENDING
- A reconciliation process can find stuck workflows : 
```sql
SELECT * 
FROM orders
WHERE state IN (
  'PAYMENT_PENDING',
  'CANCELLATION_PENDING'
  )
  AND updted_at < CURRENT_TIMESTAMP - INTERVAL '10 minutes';
```

### Exactly once misconception
- The outbox pattern does not mean : 
  - Event is delivered exactly once everywhere
- More realistic guarantees : 
  - Local database update and outbox insert -> atomic
  - Outbox publication                      -> at least once
  - Consumer processing                     -> idempotent
  - Business result                         -> effectively once
- "Effectively once" is achieved though : 
  - Stable event IDs
  - Unique constraints
  - Inbox records
  - Idempotent updates
  - Provider idempotency
  - Reconciliation

### Outbox cleanup
- Outbox rows grow continuously
- Define a cleanup policy : 
  - Retain published events for 7-30 days
  - Archive old rows
  - Delete only after operational retention
- Do not delete immediately if you need : 
  - Replay
  - Audit
  - Failure investigation
  - Reconciliation

### Transaction boundaries
- Correct : 
  - Local business update
  - outbox insert
- in one transaction
- Not guaranteed atomic : 
  - Database update 
  - Kafka publish
- Also be careful with : 
  - External API call inside database transaction
- This can hold database locks while waiting one  remote service

### When to use 
#### Orchestration
  - prefer when
    - Workflow has many steps
    - Compensation are complex
    - Timeout and deadline matter
    - business process needs visibility
    - Operators need to resume workflow
  - Example : 
    - Travel booking : 
      - reserve flight
      - reserve hotel
      - reserve car
      - change payment
#### Choreography
  - prefer when  :
    - few steps Simple event reactions
    - Loose fan-out
    - No complex compensation
    - Services can evolve independently
  - Example  :
    - OrderCreated
      - Analytics records event
      - Notification sends email
      - Search updates index

### Observability for Sagas
- Track :  
  - Saga ID
  - Order ID
  - Event ID
  - Current state
  - Transition history
  - step latency
  - Retry count
  - Compensation count
  - Stuck workflow age
- Tracing should connect : 
  - Original request
  - Outbox publication
  - Kafka record
  - Consumer processing
  - Compensation

### Answer summary
```text
I would implement the order workflow as a Saga of local transactions. The order service would save the order
and an OrderCreated outbox event in on database transaction. An outbox publisher would publish the event to
Kafka with at-least-once delivery. Inventory and Payment services would consume events idempotently using event IDs
and local inbox records, update their own databases, and write outgoing events
through their wn outbozes. The workfow would maintain explicit states and use compensating
actions such as release inventory and cancelling the order when payment fails. 
For complex workflows I would use orchestration, for simple independent reactions I would use choreography
```





```text
A Saga coordinates business workflows through local transactions and compensating actions.
The transactional outbox solves the database-plus-message dual-write problem by storing
business data and outgoing evenst atomically in one database transaction. Publication is usually
at least once, so consumers require stable event IDs, inbox or deduplication records, unique
constraints, and idempotent business operations
```

