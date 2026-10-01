# Lesson 63 : Microservices Boundaries and Communication

## Questions

1. What is a microservice>
2. How is a modular monolith different from microservices?
3. What is a bounded context?
4. What makes a good service boundary?
5. What do high cohesion and low coupling mean?
6. What does database-per-service mean?
7. Why is a shared database risky?
8. When is synchronous communication appropriate?
9. When is asynchronous communication appropriate?
10. What is the difference between a command and an event?
11. What does an API gateway do?
12. What is a backend-for-frontend?
13. What is service discovery?
14. What should a service contract contain?
15. Why is backward compatibility important?
16. Why are distributed transactions difficult?
17. What is a saga?
18. Compare choreography and orchestration.
19. What is a distributed monolith?
20. When should an organization and avoid microservices?
21. Why is a data ownership important?
22. What resilience mechanisms belong at service boundaries

## My summary

### What is a microservice ?
- A microservice is an independently deployable service that owns a focused business capability
- Examples : 
  - Order service
  - Payment service
  - Inventory service
  - Notification service
  - Identity service
- A microservice should generally own : 
  - Business capability
  - Business rules
  - Data ownership
  - Deployment lifecycle
  - Operational responsibility
- Microservices are not simply :
  - One class = one service
  - One database table = one service
  - One team member = one service
- The boundary should follow business responsibilities

### Monolith vs microservices
- Monolith
  - One deployable application
    - Orders
    - Payments
    - Inventory
    - Notification
  - Advantages : 
    - Simple local calls
    - Simple transactions
    - Simple deployment
    - Easy debugging
  - Disadvantages : 
    - Large deployment unit
    - Tight coupling over time
    - Scaling is less selective
    - One failure may affect more functionality
- Microservices
  - Order service
  - Payment service
  - Inventory service
  - Notification service
  - Advantages
    - Independent deployment
    - Independent scaling
    - Clear ownership
    - Failure isolation
    - Technology flexibility
  - Disadvantages : 
    - Network failure
    - Distributed transactions
    - Operational complexity
    - Eventual consistency
    - More complex debugging
    - Data duplication
- Microservices should solve a real organization or scaling problem
- They are no automatically better than a modular monolith

### Modular monolith as an alternative
- A modular monolith separates business modules inside one deployable applications
- One application
  - orders module
  - payments module
  - inventory module
  - notification module
- Modules have : 
  - Clear interfaces
  - Restricted dependencies
  - Separate packages
  - Explicit ownership
- This provides many architectural benefits without immediately introducing network calls
- A modular monolith is often a good starting point when:
  - The team is small
  - The domain is still changing
  - Operational maturity is limited
  - Independent scaling is not yet needed
- A well-structured modular monolith can later be split along established boundaries

### Bounded context
- A bounded context is a boundary within which : 
  - A domain has a specific meaning
  - Business terms are consistent
  - Rules are defined
- The same word can mean different things in different context
- Example : 
  - Customer context : 
    - Customer = person who owns an account
  - Order context : 
    - Customer = buyer associated with an order
  - Shipping context
    - Customer = delivery recipient
- Do not force one global model across all services
- Each bounded context may define its own : 
  - Entities
  - Value objects
  - Statuses
  - Identifiers
  - Events
  - Data model

### Identifying service boundaries
- Ask
  - What business capability does this service own?
  - Which decisions belong here?
  - Which data does it control?
  - Which team understands it?
  - Can it be deployed independently?
  - Can it scale independently?
- Good candidate boundaries often have : 
  - High internal cohesion
  - Low external coupling
  - Independent business rules
  - Clear data ownership
  - Distinct change frequency
- Poor boundaries often have : 
  - Constant cross-service calls
  - Shared transactions
  - Shared tables
  - Unclear ownership
  - Many synchronous dependencies

### High cohesion and low coupling

- High cohesion
  - Related logic stays together
    - Payment authorization
    - Payment status
    - Payment reconciliation
    - Payment provider integration
- Low coupling
  - The service does not depend on internal details of other service
  - Bad : 
    - Order service directly updates Payment service database
  - Better : 
    - Order service requests payment through an API or publishes an event
- A useful test : 
  - if on service changes an internal table or implementation, how many other services ust change?
- If the answer is many, coupling is too high

### Database-per-service
- A common microservice principle is that each service owns its data store
  - Order Service     -> Orders database
  - Payment Service   -> Payments database
  - Inventory Service -> Inventory database
- Other services interact through : 
  - APIs
  - Events
  - Queries through an owned interface
- Advantages : 
  - Clear ownership
  - Independent schema evolution
  - Reduced coupling
  - Independent scaling
- Disadvantages :
  - Cross-service queries become harder
  - Distributed transaction are needed
  - Data duplication is common
  - Reporting requires aggregation pipelines
- The rule is not necessarily : 
  - Every service must use a different database technology
- It means
  - Other services should not directly own or mutate the service's tables

### Shared database anti-pattern
- Shared database
  - Order Service
  - Payment Service
  - Inventory Service
- Problems : 
  - Schema changes affect multiple services
  - Ownership is unclear
  - Services bypass business rules
  - Transaction become tightly coupled
  - Independent deployment becomes difficult.
- A shared database may be acceptable during migration or in a modular monolith, but it weakens microservices boundaries

### Synchronous communication
- Synchronous communication waits for a response
  - Order Service
    - HTTP/gRPC
  - Payment Service
    - response
  - Order Service continues
- Use synchronous calls when : 
  - The caller needs an immediate answer
  - The operation is part of the request decision
  - The dependency is expected to respond quickly
  - The interaction is naturally query-like
- Examples: 
  - Get user profile
  - Check current shipping options
  - Validate access permission
  - Retrieve product details
- Risks : 
  - Added latency 
  - Dependency failure
  - Timeout propagation
  - Cascading failures
  - Runtime coupling

### Asynchronous communication
- Asynchronous communication sends a messages or event without waiting for immediate processing
  - Order Service
    - event
  - Message broker
  - Payment Service
- Use asynchronous communication when : 
  - The work can complete later
  - The caller does not need an immediate result
  - Traffic should be buffered
  - Multiple consumers need the event
  - Services should be loosely coupled
- Example : 
  - Send email
  - Generate report
  - Update search index
  - Record analytics
  - Process payment asynchronously
  - Publish order-created event
- Risks :
  - Eventual consistency
  - Duplicate delivery
  - Ordering concerns
  - Retry complexity
  - Harder debugging
  - Delayed error visibility

### Request vs event
- Request
  - A request asks another service to perform an operation
    - Authorize this payment
  - The caller usually expects a response
- Event
  - An event describes something that already happened
    - PaymentAuthorized
  - Consumers decide what to do with it

- Difference
  - Command / request : 
    - "Do this."
  - Event : 
    - "This happened."
- Avoid naming events as commands : 
  - Bad event : 
    - ProcessPayment
  - Better event
    - PaymentRequested
    - PaymentAuthorized
    - PaymentFailed

### Command and event messaging

- Command
  - Directed to one logical owner
    - ReserveInventory
  - Usually :
    - One intended consumer
    - Action-oriented
    - May be rejected
- Event
  - Published for interested consumers : 
    - InventoryReserved
  - Usually : 
    - Multiple consumers
    - Past-tense fact
    - Should describe completed or observed state
- This distinction improves messages semantics and ownership


### API Gateway
- An API gateway provides a single entry point for external clients
  - Mobile APP
  - Web APP
  - Partner
    - API Gateway
      - Order service
      - User service
      - Catalog service
- Responsibilities may inclues : 
  - Routing
  - Authentication
  - TLS termination
  - Rate limiting
  - Request size limits
  - Protocol translation
  - Response aggregation
  - Observability
- Do not put all business logic in gateway.
- An overloaded gateway can become : 
  - A bottleneck
  - A single failure domain
  - A large business-logic monolith
- Keep gateway logic mostly cross-cutting and routing-oriented.


### Backend-for-Frontend
- A backend-for-frontend, or BFF, is tailored to one client type
  - Web BFF
  - Mobile BFF
  - Partner BFF
- Each BFF can provide : 
  - Client-specific aggregation
  - Response shaping
  - Client-specific caching
  - Protocol adaptation
- This avoids forcing one generic API to satisfy every client
- Use BFFs when : 
  - Clients have very different data needs
  - Mobile requires fewer payloads
  - Web needs richer aggregation
  - Partner APIs require different contracts

### Service discovery
- In a dynamic environment, service instances may change : 
  - payment-1
  - payment-2
  - payment-3
- Service discovery maps a logical service name to reachable instances : 
  - payment-service
  - Instance registry
    - 10.0.0.11
    - 10.0.0.12
    - 10.0.0.13
- Discovery can be : 
  - Client-side
    - The client queries the registry and choose and instance
  - Server-side
    - A load balancer or proxy queries the registry and routes requests
  - Platform-provided
  - DNS-based
  - Service-mesh-based
- The exact mechanism depends on the deployment platform

### Service to Service communication choices

#### HTTP/REST
- Advantages
  - Widely understood
  - Easy debugging
  - Browser and tooling support
- Disadvantages : 
  - Text overhead
  - Flexible contracts may become inconsistent

#### gRPC
- Advantages : 
  - Compact binary protocol
  - Strong schemas
  - efficient internal communication
  - Streaming support
- Disadvantages : 
  - More tooling complexity
  - Less directly browser-friendly
  - Requires schema management

#### Message broker
- Advantages : 
  - Asynchronous processing
  - Buffering
  - Replay
  - Fan-out
  - Decoupling
- Disadvantages
  - Eventual consistency
  - Operational complexity
  - Duplicate and ordering concerns

- Choose based on interaction semantics, not fashion

### Contract design
- A service contract defines : 
  - Request format
  - Response format
  - Errors
  - Authentication
  - Timeout expectations
  - versioning
  - Compatibility rules
- For synchronous API document : 
  - GET /v1/orders/{id}
- For events, document : 
  - OrderCreated
  - OrderPaid
  - OrderCancelled
  - Include : 
    - Event ID
    - Event type
    - Version
    - Occurred-at timestamp
    - Aggregate ID
    - Payload
- Example : 
```json
{
  "eventId" : "evt-123",
  "eventType" : "OrderCreated",
  "eventVersion" : 1,
  "aggregatedId" : "order-1001",
  "occurredAt" : "2026-09-30T12:00:00Z",
  "payload" : {
    "customerId" : "customer-34",
    "total" : 80.78
  }
}
```

### Backward compatibility
- Prefer additive changes : 
  - Add optional response field
  - Add optional event field
  - Add new endpoint
  - Add new event type
- Risky changes : 
  - Rename a field
  - Change a field type
  - Remove a field
  - Change semantic meaning
  - Make optional data mandatory
- For event consumers : 
  - Old consumer must tolerate new optional fields
  - New consumer should handle older event versions
- Possible strategies : 
  - Schema version field
  - Versioned topics
  - Content negotiation
  - Schema Registry
  - Consumer-driven contracts

### Distributed transactions
- A transaction across services is difficult : 
  - Order database
  - Payment database
  - Inventory database
- A local database transaction cannot automatically include all three
- Avoid : 
  - One global transaction across every service
- Prefer patterns such as : 
  - Saga
  - Transactional outbox
  - Compensating action
  - State machine
  - Idempotent consumer
- Example order workflow : 
  - OrderCreated
  - InventoryReserved
  - PaymentAuthorized
  - OrderConfirmed
- Failure : 
  - PaymentFailed
  - Release inventory
  - Cancel order


### Saga Pattern
- A saga is a sequence of local transactions coordinated across services
- Choreography
  - Services react to events : 
    - Order Service publishes OrderCreated
    - Inventory Service reserves stock
    - InventoryReserved event
    - Payment Service charges payment
  - Advantages : 
    - Less central orchestration
    - Services react independently
  - Disadvantages : 
  - Flow can become difficult to understand 
  - Hidden coupling through events
  - Harder failure visibility
- Orchestration
  - A coordinator directs the workflow : 
    - Order Safa Orchestrator
      - Reserve inventory
      - Authorize payment
      - Confirm order
  - Advantages : 
    - Central workflow visibility
    - Explicit failure handling
    - Easier process reasoning
  - Disadvantages : 
    - Orchestrator becomes important infrastructure
    - Potential coupling to service operations
- Use choreography for simple flows and orchestration when workflows are complex or business critical

### Resilience at service boundaries
- Every synchronous service call should define
  - Timeout 
  - Retry policy
  - Circuit breaker
  - Bulkhead
  - Fallback 
  - Metrics
- Example : 
  - Order service -> Payment service
  - Possible policy : 
    - Time out : 500ms
    - Retry : one retry for connection reset
    - Circuit breaker : open after repeated failures
    - Bulkhead : separate payment connection pool
    - Fallback ; mark payment as PENDING
  - Avoid retrying payment blindly without idempotency

### Distributed tracing
- A request may cross multiple services : 
  - Gateway
  - Order service
  - Payment service
  - Database
- Propagate  :
  - Trace ID
  - Span ID
  - Request ID
- Tracing helps answers  :
  - Where did latency occur ?
  - Which dependency failed ?
  - Which services handled the request ?
- Logs should include : 
  - traceId
  - spanId
  - service
  - operation
  - entity ID
  - outcome
- Do not put sensitive payloads into trace attributes

### Data duplication
- Microservices often duplicate read data : 
  - Catalog service owns product data
  - Search service stores search projections
  - Order Service stores product snapshot
- This can be correct of each copy has a purpose
- Examples : 
  - Order stores price at purchase time
  - Search stores searchable product fields
  - Analytics stores aggregated events
- Duplication introduces : 
  - Replication lag
  - Update events
  - Rebuild procedures
  - Schema evolution
- The key is to distinguish  : 
  - Source of truth
  - Read projection
  - Historical snapshot
  - Cache

### Service ownership

- For each important data item, identify : 
  - Who creates it?
  - Who updates it?
  - Who validates it?
  - Who publishes its changes?
  - Who handles conflicts?
- Example  :
  - Payment service owns payment status
  - Order service may store payment-status project
  - Only payment service changes the authoritative payment status
- This prevents multiple services frm independently changing the same business fact

### Microservices anti-patterns

#### Distributed monolith
- Services are deployed separately but : 
  - Every request calls every service
  - All services must deploy together
  - All services share one database
- This combines distributed system cost with monolith coupling

#### Shared database ownership
- Multiple services directly modify the same tables

#### Synchronous chain
- One request depends on every service
  - `A -> B -> C -> D -> E`

#### Chatty communication
- `One user request -> 50 service calls`

### Nano-services
- Services are too small to own meaningful business capabilities

#### Centralized business gateway
- The gateway contains most business logic

#### Event without ownership
- Many services react to an event but no service clearly owns the resulting state.


### When not to use microservices
- A monolith may be better when :
  - Domain is not understood
  - Team is small
  - Traffic is modest
  - Deployment independence is unnecessary
  - Operations team is limited
  - Transactions across most modules
- Microservices add : 
  - Network calls
  - Service deployment
  - Monitoring
  - Security
  - Data consistency work
  - Failure handling
- Adopt them when the benefits justify those costs


### Example : Order-processinf architecture
- architecture
  - Client
  - API Gateway
  - Order service
    - Order database
    - publishes OrderCreated
      - Message Broker
        - Inventory
          - Inventory Database
        - Payment service
          - Payment database
        - Notification service
          - Notification provider
- Workflow : 
1. Order service creates order with PENDING status
2. Publishes OrderCreated
3. Inventory service reserves inventory
4. Publishes InventoryReserved or InventoryRejected
5. Payment service authorizes payment
6. Publishes PaymentAuthorized or PaymentFailed
7. Order Service confirms or cancels order
8. Notification service sends customer updates

- Required protections : 
  - Idempotent consumers
  - Transactional outbox
  - Retries with BackOff
  - Dead-letter handling 
  - Timeouts
  - Circuit breakers
  - Tracing

### Answer summary : 
- concise answer : 
```text
I would define microservice boundaries around business capabilities and bounded context, 
not technical layers. Each service should own its business rules and authoritative data, while other
services communicate through documented APIs or events. Synchronous calls are appropriate
when an immediate response is required, while asynchronous events are better for decoupled 
background work and fan-out. I would avoid shared database writes, use idempotent
consumers and transactional outbox publishing, and handle distributed workflows with sagas or explicit state machines
Every service boundary needs timeouts, retries, circuit breaker
observability, and backward compatible contracts
```  



```text
Microservices should be organized around business capabilities and bounded contexts.
Each service should own its business rules and authoritative data, while communication occurs
through explicit synchronous APIs or asynchronous events. Good designs avoid shared
database ownership, use backward-compatible contracts, apply sagas for distributed workflows,
and protect service boundaries with timeouts, retries, idempotency, circuit breaker, and 
Observability
```