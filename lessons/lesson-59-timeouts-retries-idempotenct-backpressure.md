# Lesson 59 : Timeouts, Retries, Idempotency, and Backpressure

## Questions

1. Why does every remote call need a timeout?
2. What is the difference between a connection timeout and a read timeout?
3. What is a deadline?
4. Why does a timeout not prove that an operation failed?
5. Which failure should usually be retried?
6. What is exponential backoff?
7. Why is jitter useful?
8. What is retry storm?
9. What is idempotency? 
10. What is idempotency keys? 
11. Why must idempotency keys be protected by uniqueness constraints ?
12. How should a server handle the same key with a different request body 
13. What is an idempotent consumer?
14. What is backpressure?
15. Why are unbounded queues dangerous?
16. What is load shedding?
17. Compare token bucket and leaky bucket rate limiting
18. How does Kafka consumer lag indicate backpressure?
19. Why can retries multiply across service layers ?
20. How should retires, timeouts, circuit breaker, and backpressure work together?


## My summary

- Why timeouts are necessary
  - A remote call can wait indefinitely if no timeout is configured
  - without a timeout
    - Request thread waits
    - more requests arrive
    - threads become occupied
    - thread pool is exhausted
    - service A becomes unavailable
  - every remote operation should have a bounded time limit
    - remote call
      - connection timeout
      - read timeout
      - overall request deadline
  - A timeout is a resource-protection mechanism, not merely an error-handling mechanism
- Types of timeouts
  - Connection timeout
    - maximum time to establish a network connection
  - Read timeout
    - Maximum time waiting for response data after the connection is established
  - Request timeout
    - Maximum duration for the complete operation
      - DNS + connection + request transmission + server processing + response
  - Queue timeout
    - Maximum time work may wait in an internal queue before execution
  - Database timeout
    - Maximum time allowed for a database operation or transaction.
- Timeout selection
  - should be based on :
    - Expected latency
    - Dependency behavior
    - User-facing deadline
    - Retry budget
    - Business importance
  - suppose the client has total deadline of 2 seconds
    - Gateway : 2.0 seconds
    - Service A: 1.8 seconds
    - Service B: 1.2 seconds
    - Database: 800 ms
  - the inner operations should not outlive the outer request deadline.
  - Bad design :
    - Gateway timeout: 2 seconds
    - Service A retries Service B three times
    - Each retry timeout: 2 seconds
  - The caller has already timed out while the service continues consuming resources
- Deadlines versus independent timeouts
  - An independent timeout gives each operation its own limit
    - Call A: 1 second
    - Call B: 1 second
    - Call C: 1 second
  - Deadlines are often better for multi-service request chains because they prevent downstream work from continuing after the original request can no longer succeed.
  - conceptually : 
    - remainingTime = deadline - currentTime
- What happens after a timeout?
  - A timeout creates ambiguity
    - Client sends request
    - Server processes request
    - Response is lost
    - Client times out
  - Client sees
    - operation timeout
    - but operation may have succeeded
  - Therefore
    - timeout != operation did not happen
- Retry only appropriate failure
  - some failure may be temporary:
    - Connection reset
    - Temporary network error
    - 503 SERVICE UNAVAILABLE
    - 429 TOO MANY REQUESTS
    - Leader election in progress
  - some failures are usually not retryable : 
    - Invalid request
    - Authentication failure
    - Authorization failure
    - Malformed event
  - Retryable : 
    - temporary infrastructure failure
  - Non-retryable:
    - invalid input or permanent business failure
- Retry storms
  - Assume 
    - service capacity : 1,000 requests/second
    - Incoming traffic : 1,200 requests/second
  - every failed request retries twice : 
    - Original requests: 1,200
    - First retries    : 200
    - Second retries   : 200
    - Total pressure   : 1,600
  - dependency is already overloaded, but retries increase the load
  - retries should reduce pressure, not amplify it
- Exponential backoff
  - increases the delay between attempts
  - example : 
    - Attempt 1 -> immediate
    - Attempt 2 -> 100 ms
    - Attempt 3 -> 200 ms
    - Attempt 4 -> 400 ms
    - Attempt 5 -> 800 ms
  - Formula:
    - delay = initialDelay x 2 ^ attempt
  - use a max delay, without a cap , it may grow unreasonably
- Jitter
  - If many clients retry at the same time, exponential backoff alone can synchronize them
    - 100 client fail at 10:00:00
    - 100 client retry at 10:00:01
    - creates another traffic spike
  - Jitter randomizes retry timing
    - Client A -> 820 ms
    - Client B -> 1,120 ms
    - Client C -> 940 ms
  - common strategies
    - Full Jitter
      - random delay between 0 and calculated max
    - Equal Jitter
      - half calculated delay + random value form remaining range
    - Decorrelated Jitter
      - next delay depends on previous randomized delay
- Retry budget
  - A retry policy should limit
    - max attempts
    - max elapsed time
    - max total retry traffic
  - overall deadline is often more useful than a fixed attempt count alone
- Retrying HTTP operations
  - Usually safer
    - GET
    - PUT
    - DELETE
  - commonly designed to be idempotent, though implementation still matter
  - Potentially dangerous
    - POST /payments
    - POST /orders
    - POST /emails
  - protect such operations with
    - Idempotency key
    - Unique business key
    - deduplication record
    - Provider-side idempotency
- Idempotency
  - An operation is idempotent when repeating it produces the same intended result as executing it once
  - example :
    - Set use status to ACTIVE
    - non-idempotent
      - increment account balance by 10
- Idempotency keys
  - A client sends a unique key
  - server records
    - key -> request fingerprint -> result
  - first request stores result, retry returns original result
  - robust implementation stores :
    - idempotency_key
    - request_hash
    - operation_status
    - response_status
    - response_body
    - create_at
- Reusing an idempotency key incorrectly
  - Suppose a client sends
    - key : abc123
    - amount : 100
  - then retries
    - Key : abc123
    - amount : 500
  - same key was used with a different request
    - server should reject
      - 409 CONFLICT
    - another documented client error
  - An idempotency key should represent one logical operation, not an arbitrary reusable identifier
- Concurrent duplicate requests
  - Two requests may arrive simultaneously with the same key
    - Request A -> key abc123
    - Request B -> key abc123
  - Unsafe implementation : 
    - A checks : key absent
    - B checks : key absent
    - A processes
    - B processes
  - Use atomic uniqueness constraint 
  - possible flow :
    - Attempt to insert key
    - One request succeeds
    - Other request sees duplicate
    - Other request waits or returns stored result
- Idempotent Consumers
  - Kafka and other brokers may deliver a message more than one
    - event delivered
    - consumer processes event
    - offset commit fails
    - Event delivered again
  - Use a processed event record
  - Consumer flow
    - Begin transaction
    - Insert event Id
      - duplicate -> stop safely
      - new       -> process business operation
    - commit transaction
- Backpressure
  - It controls a producer when consumers cannot keep up
    - producer rate : 10,000 messages/sec
    - consumer rate : 5,000 messages/sec
  - without backpressure
    - Queue grows continuously
    - Memory usage increases
    - Latency increases
    - System eventually fails
  - backpressure makes the producer slow down, reject work, or reduce the amount of data generated
- Bounded queues
  - An unbounded queue is dangerous
  - The queue may grow until memory is exhausted
  - Prefer bounded queue
  - When full, choose a policy
    - Reject
    - Block producer
    - Drop oldest
    - Drop newest 
    - Run in caller thread
    - Persist to durable storage
  - correct policy depends on business importance
- Queue-full policies
  - Reject
    - Return an error immediately
    - useful when the caller can retry or defer work
  - Block
    - Producer waits until capacity is available
    - useful when slowing producer is safe
    - Risk:
      - blocked producer threads can exhaust the caller
  - Drop newest
    - Preserve existing work
    - Reject newly arrived work
    - useful when older queued work is more valuable
  - Drop oldest
    - Discard stale queued work
    - Accept newest work
    - useful for current-stale updates such as telemetry
  - Caller-runs
    - Caller executes the task directly
    - this naturally slows the caller but can unexpectedly increase request latency
- Load shedding
  - When system has no capacity, deliberately reject lower-priority work
  - return appropriate result
    - 429 TOO MANY REQUESTS
    - 503 SERVICE UNAVAILABLE
- Rate Limiting
  - It controls the max request rate
    - 100 requests per minute per user
  - common algo : 
    - Fixed window
      - 00:00-00:59 -> 100 requests
      - 01:00-01:59 -> 100 requests
      - simple but clients may burst at window boundaries
    - Sliding window
      - Counts request over a continuously moving interval
      - More accurate, but more expensive
    - Token bucket
      - Tokens are added at a fixed rate
      - Bucket capacity : 100
      - Refill rate     : 10 tokens / sec
      - each request consume one token
      - allows controlled bursts up to bucket capacity
    - Leaky bucket
      - processes request ata relatively constant rate
      - useful for smoothing traffic
- Backpressure in kafka
  - Kafka provides several natural buffering mechanism: 
    - Producer batching
    - Broker retention
    - Partitioning
    - Consumer polling
    - Consumer lag
  - But kafka does not make consumers automatically fast enough
  - If consumers fall behind
    - consumer lag increases
  - Do not increase consumer count beyond useful partition parallelism

```text
Timeouts limit resource usage, retries recover from temporary failures, idempotency prevents duplicate side effects
and backpressure prevents producers from overwhelming consumers
These mechanisms must be designed together using deadlines, bounded retries, exponential backoff with jitter,
unique constraints, bounded queues, and explicit overload behavior
```