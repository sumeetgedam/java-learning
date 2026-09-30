# Lesson 62: System Design - Distributed Rate Limiter

## Questions

1. What problem does a rate limiter solve?
2. Why is a local rate limiter insufficient in a horizontally scaled service?
3. What is a fixed-window counter?
4. What is the fixed-window boundary problem?
5. What is a sliding-window log?
6. What is a sliding-window counter?
7. How does a token bucket work?
8. How does a leaky bucket differ from a token bucket?
9. Why must distributed rate-limit decisions be atomic?
10. Why is Redis commonly used for rate limiting?
11. What should a 429 response contain?
12. What dimensions can form a rate-limit key?
13. What is hierarchical rate limiting?
14. What is the difference between rate limiting and concurrency limiting?
15. What is the difference between a rate limit and a quota?
16. What are fail-open and fail-closed behavior?
17. What is a hot rate-limit key?
18. When is approximate limiting acceptable?
19. Why should retry clients respect Retry-After?
20. What metrics should be monitored?

## My summary

### What is rate limiting ?
- A rate limiting controls how many operations a client can perform during a period
- example : 
  - 100 requests per minute per user
  - when limit is exceeded :
    - 429 Too Many Requests
- Rate limiting protects :
  - Application servers
  - Databases
  - External providers
  - Message brokers
  - Expensive operations
  - Individual users from abuse

### Why distributed rate limiting ?
- A local rate limiter only knows about one application instance.
  - Instance A : 
    - User 42 -> 80 requests
  - Instance B : 
    - User 42 -> 80 requests
  - If intended limit is 100 requests per minute globally : 
    - combined requests = 100
- A local limiter allows the client to exceed the global limit.
- A distributed rate limiter shares counters or token state : 
  - Distributed rate-limit store
    - Instance A
    - Instance B
    - Instance C
- Common shared stores : 
  - Redis
  - Distributed key-value store
  - API gateway
  - Service mesh
  - Database, for low-throughput use cases

### Requirements

#### Functional Requirements
- The rate limiter should :
  - Allow requests under the configured limit
  - Reject requests over the limit
  - Identity the client
  - Track usage
  - Return retry information
  - Support different policies
- Possible identity dimensions :
  - User ID
  - API key
  - IP Address
  - Tenant ID
  - Endpoint
  - HTTP method
  - Combination of dimensions

#### Non-functional Requirements
- Assume: 
  - Very low latency
  - High availability
  - Atomic decisions
  - Horizontal scalability
  - Bounded memory usage
  - Acceptable behavior during store failure
- example target :
  - Rate-limit decision p99 < 5ms
- The limiter is usually on the request path, so it must be fast

### Scope and policy examples
- Possible policies :
  - 100 requests / minute per user
  - 1,000 requests / minute per API key
  - 10 requests / second per IP
  - 5 payment attempts / minute per account
- Different endpoints may have different limits :
  - GET /products
    - 1,000 requests/minute
  - POST /payments
    - 10 requests/minute
  - POST /login
    - 5 attempts/minute
- A key may combine dimension:
  - rate-limit:user-42:POST:/payments

### High level architecture

- arch flow
  - Client
  - API Gateway / Load Balancer
  - Rate-Limit Middleware
  - Distributed Rate-Limit Store
  - Application Service
- Request flow :
  - Identify client
  - Build rate-limit key
  - Automatically evaluate policy
  - If allowed, forward request
  - If rejected, return 429
- The rate limiter should execute before expensive business processing
  - Request
  - Rate limiter
    - Allowed -> application
    - rejected -> 429

### Fixed-window algorithm
- A fixed window counts requests inside a fixed interval.
- Example : 
  - Limit: 100 requests/minute
  - 12:00:00-12:00:59 -> 100 requests
  - 12:01:00-12:01:59 -> 100 requests
- Store :
  - counter key
  - window expiration
- Example key :
  - rate:user:42:202609291200
- Algorithm :
  - Determine current window
  - Increment counter
  - Set expiration if key is new
  - Allow if counter <= limit
  - Reject otherwise
- Advantages
  - Simple 
  - Fast 
  - Memory-efficient
  - Easy to implement
- Disadvantages
  - Boundary burst problem:
    - 12:00:59 -> 100 requests
    - 12:01:00 -> 100 requests
  - The client sends 200 requests within approximately two seconds despite a limit of 100 per minute
- Fixed windows are often acceptable for coarse protection but are not perfectly smooth.

### Sliding-window log
- A sliding-window log stores timestamps of recent requests
- For each client : 
  - [user42] -> [12:00:10, 12:00:15, 12:00:21, ......]
- For every request : 
  - Remove timestamps older than the window
  - Count remaining timestamps
  - Reject if count >= limit
  - Add current timestamp if allowed
- Advantages
  - Accurate rolling-window behavior
  - Avoids fixed-window boundary burst
- Disadvantages
  - Stores one entry per request
  - Higher memory usage
  - More expensive at high traffic
  - Requires atomic cleanup and insertion
- This may be appropriate for :
  - Low-volume sensitive operations
  - Login attempts
  - Password reset requests
  - Payment attempts

### Sliding-window counter
- A sliding-window counter approximates the current window using multiple sub-windows
- Example : 
  - Limit: 100 requests / minute
  - Sub-windows: 6 x 10 seconds
- Estimate current usage using:
  - current sub-window count + Weighted previous sub-window counter
- Advantages
  - Less memory than timestamp logs
  - More accurate than one fixed window
- Disadvantages
  - Approximation
  - More complex calculation
  - Requires multiple counters
- This is a useful compromise between accuracy and cost

### Token-bucket algorithm
- The token bucket is one of the most widely useful rate-limiting algorithms
- Configuration  
  - Bucket capacity: 100 tokens
  - Refill rate: 10 tokens/second
- Each request consumes one token:
  - Token available -> allow
  - No token        -> reject
- The bucket refills over time :
  - Time passes 
  - Tokens are added
  - Maximum capacity is enforced
- Example : 
  - Capacity = 100
  - Refill = 10 tokens / sec
- The client can : 
  - Burst up to 100 requests
  - Then sustain approx 10 requests/sec
- Advantages
  - Supports controlled bursts
  - Smooth long-term rate
  - Efficient state representation
  - Work well for APIs
- Disadvantages
  - Requires accurate time calculations
  - Needs atomic updates
  - Policy behavior can be misunderstood

### Leaky-bucket algorithm
- The leaky bucket processes requests at a fixed rate
  - Incoming requests
  - Queue
  - Fixed processing rate
  - If queue is full :
    - Reject request
- Advantages
  - Smooth output rate
  - Protects downstream services
  - Useful for traffic shaping
- Disadvantages
  - Can add queueing latency
  - Requires queue storage
  - May reject bursts
- Token bucket versus leaky bucket

| Feature      | Token bucket              | Leaky bucket                     |
|--------------|---------------------------|----------------------------------|
| Burst        | Allows controlled bursts  | Usually smooths bursts           |
| State        | Token count and timestamp | Queue                            |
| Main purpose | Rate limiting             | Traffic shaping                  | 
| Latency      | USually lower             | May queue requests               |
| Good for     | API request limits        | Fixed-rate downstream processing |

### Choosing an algorithm
| Requirement                  | Suitable algorithm     |
|------------------------------|------------------------|
| Simple coarse protection     | Fixed Window           |
| Precise recent-request limit | Sliding-window log     |
| Efficient approximation      | Sliding-window counter |
| Controlled bursts            | Token bucket           |
| Smooth fixed output rate     | Leaky bucket           |

- For a general API
  - Token bucket is often a strong default
- For login protection
  - Sliding window or token bucket
- For sending to a provider with strict fixed capacity
  - Leaky bucket or token bucket

### Redis-based architecture
- Redis is commonly used because it provides
  - Low latency access
  - Atomic command
  - Expiration
  - Lua scripting
  - Shared state
- Example conceptual key : 
  - rl:{tenantId}:{userId}:{endpoint}
- Stored state may contain : 
  - tokens
  - last_refill_time
- For a fixed window
  - INCR key
  - EXPIRE key 60
- But this must be implemented carefully so expiration is set atomically only when the key is created

### Token bucket state
- A Token bucket record might contain :
  - tokens
  - lastRefillTimestamp
- Example : 
```json
{
  "tokens" : 37,
  "lastRefillTimestamp" : 1790670000000
}
```
- For a new request : 
  - elapsed = now - lastRefillTimestamp
  - newTokens = elapsed x refillRate
  - tokens = min(capacity, tokens + newTokens)
- then
```python
if tokens >= requestedCost:
    tokens -= requestedCost
    allow
else:
    reject 
```
- The read-modify-write operation must be atomic

### Why atomicity matters
- Unsafe flow
  - Instance A reads tokens = 1
  - Instance B reads tokens = 1
  - Instance A allows request
  - Instance B allows request
- Both requests consumed the same token
- Correct flow:
  - Atomic script
    - read state
    - refill
    - decide
    - update state
    - return result
- use :
  - Redis Lua script
  - Redis transaction, where appropriate
  - Atomic server-side command
  - Database conditional update
- A distributed rate limiter must make the decision and state update one logical atomic operation

### Redis Lua conceptual example
```Lua
local tokens = redis.call("HGET", KEYS[1], "tokens")
local last  = redis.call("HGET", KEYS[1], "last")

local now = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local refill_rate = tonumber(ARGV[3])
local request = tonumber(ARGV[4])

tokens = tonumber(tokens) or capacity
last = tonumber(last) or now

local elapsed = math,max(0, now - last)
local refilled = elapsed * refill_rate
tokens = math.min(capacity, tokens + refilled)

local allowed = 0
if tokens >= requested then
    tokens = tokens - requested
    allowed = 1
end    
   
redis.call("HSET", KEYS[1],
            "tokens", tokens,
            "last", now)
            
return {
    allowed,
    tokens
} 
```

- This is conceptual, production scripts should also handle : 
  - Key expiration
  - Numeric precision
  - Clock units
  - Malformed state
  - Script deployment
  - Monitoring

### Rate-limit response
- A rejected request should communicate the result :
```http
HTTP/1.1 429 Too Many Requests
Retry-After: 3
```
- Possible headers : 
  - X-RateLimit-Limit
  - X-RateLimit-Remaining
  - X-RateLimit-Reset
  - Retry-After
- Example : 
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1790670060
Retry-After: 3 
```
- These headers help clients behave correctly instead of retrying immediately

### Identify and key design
- Possible keys : 
  - rl:user:42
  - rl:api-key:abc123
  - rl:ip:203.0.113.10
  - rl:tenant:acme
  - rl:user:42:endpoint:payments
- Became with IP-based limiting
  - Many users may share one NAT IP
  - One user may use multiple IPs
  - Proxies may obscure the real client
- A common policy combines dimensions : 
  - Per-user limit
  - AND
  - Per0IP limit
  - AND
  - Global endpoint limit
- For example : 
  - User 42:
    - 100 requests/minute
  - IP address:
    - 1,000 requests/minute
  - All payment requests:
    - 10,000 requests/minute
- A request must pass all applicable limiters

### Hierarchical rate limiting
- A system may have multiple levels : 
  - Global limit
  - Tenant limit
  - User limit
  - Endpoint limit
- Example : 
  - Global API       : 100,000 requests/sec
  - Tenant           : 10,000 requests/sec
  - User             : 100 requests/minute
  - Payment endpoint : 10 requests/minute
- This protects both the entire service and individual clients
- Potential issue : 
  - Multiple rate-limit checks increase latency
- Optimize by:
  - Combining checks in one script
  - Caching static policies
  - Applying cheap checks first
  - Using gateway enforcement for coarse limits

### Distributed clock concerns
- Rate limiting depends on time
- If application instances use inconsistent clocks
  - Instance A : now = 10:00:00
  - Instance B : now = 09:59:58
- Token refill calculations may differ
- Mitigations:
  - Use Redis server time
  - Synchronize machine clocks with NTP
  - keep time calculations in one atomic script
  - Avoid relying on local wall clocks across nodes
- For Redis, use server-side time within the script where appropriate

### Fail-open vs fail-closed
- What happens if the rate-limit store is unavailable

#### Fail-open
- Allow requests : 
  - Rate limiter unavailable
  - Request proceeds
- Advantages
  - Better availability
  - No outage caused by rate-limit store
- Risks
  - Abuse protection disappears
  - Downstream services may overload

#### Fail-closed
- Reject requests : 
  - Rate limiter unavailable
  - Request rejected
- Advantages : 
  - Strong protection
  - Predictable enforcement
- Risks : 
  - Rate-limit store outage become application outage
- Choose based on endpoint criticality
- Possible policy
  - Public read endpoint -> fail-open with local emergency limit
  - Payment endpoint     -> fail-closed or conservative fallback
  - Login endpoint       -> fail-closed
- A hybrid fallback can use a local in-memory emergency limiter but local limits are not globally exact

### Rate-limiter availability
- The rate limiter itself is a critical dependency
- Deploy it with :
  - Replication
  - Failover
  - Monitoring
  - Capacity
  - Connection pooling
  - Timeouts
  - Fallback behavior
- Avoid placing a single unreplicated Redis instance on the critical request oath for a high-availability system
- Monitor
  - Decision latency
  - Allowed requests
  - Rejected requests
  - Store errors
  - Key count
  - Memory usage
  - Hot keys
  - Script execution time

### Hot keys
- A popular client or tenant may generate a hot rate-limit key : 
  - rl:user:42
- All requests for tht key may reach the same Redis slot or logical record
- Possible mitigations
  - Use local pre-filtering
  - Partition counters carefully
  - USer gateway-level limiting
  - Apply approximate local limits
  - Use hierarchical keys
  - Scale Redis appropriately
- Do not split one logical client's counter scross multiple keys unless you can preserve the intended limit semantics

### Approx vs exact limiting

#### Exact global limit
    
- Every request consults shared state
- Advantages
  - Precise
  - Consistent
  - Easy to explain
- Costs : 
  - Shared-store latency
  - Store dependency
  - Hot-key pressure

#### Approx distributed limit
- Instances use local counters or periodically synchronized state
- Advantages
  - Lower latency
  - Less shared store traffic
  - Better availability
- Costs :
  - Clients may exceed the limit temporarily
  - More complex error bounds
  - Harder to reason about
- choose based on the business
- FOr abuse protection, approx limits may be acceptable
- For strict provider quotas, exact or conservative enforcement may be necessary


### Rate limit vs concurrency limit
- These are different

#### Rate limiting
- Controls requests over time
  - 100 requests per second

#### Concurrency limiting
- Controls simultaneously
  - Maximum 20 concurrency payment calls
- A service may need both : 
  - Rate limit        : 100 requests / sec
  - Concurrency limit : 20 active requests
- Concurrency limit protects against slow requests that occupy resource for a long time


### Rate limiting vs Quotas

#### Rate limit
- Short term request rate : 
  - 100 requests/minute

#### Quota
- Longer term usage allowance
  - 1 million API calls/month
- A client may pass rate limit but exceed its monthly quota
- Both may be enforced : 
  - Short term token bucket
  - Long term usage counter

### Rate limit and retries
- A Client receiving 429 should not retry immediately
- Response :
```http
429 Too Many Requests
Retry-After: 5
```

- Client behavior :
  - wait 5 seconds
  - Apply Jitter
  - Retry only if operation is safe
- If clients ignore `Retry-After` : 
  - Rate-limit rejection
  - Immediate retry
  - More rejection
- The server and client must cooperate to avoid retry amplification

### Rate limiting expensive operations
- Not all requests should cost one token
- Assign request costs:
  - GET /health   -> cost 1
  - GET /search   -> cost 5
  - POST /report  -> cost 20
  - POST /export  -> cost 100
- Token bucket : 
  - tokens >= requestCost
- This better reflect resource usage
- A request-cost policy should be :
  - Documented 
  - Stable
  - Observable
  - Protected from client manipulation
- The server, not the client, determines the cost

### Rate limiting architecture options

#### Application middleware

```text
Client -> Application -> Redis
```
- Advantages :
  -  Business-aware
  - Can use authenticated identity
  - Easy to customize
- Disadvantages
  - Every service may implement it differently
  - Consume application resources

#### API gateway
```text
Client -> Gateway -> Application
```
- Advantages : 
  - Centralized
  - Protects services before traffic reaches them
  - Consistent policy
- Disadvantages : 
  - May not know business context
  - Gateway becomes critical infrastructure

#### Service mesh
- useful for:
  - service-to-service limits
  - Standardized infrastructure behavior
- Business-specific limits may still belong in application code


### Failure scenario table
| Failure                          | Possible behavior                                 |
|----------------------------------|---------------------------------------------------|
| Redis unavailable                | Fail-open, Fail-closed, or emergency local limit  |
| One application instance fails   | Other instances continue using shared state       |
| Clock drift                      | User server time or synchronized clocks           | 
| Hot Key                          | Local pre-filtering or capacity scaling           | 
| Client ignores 429               | Continue rejecting and alert on abuse             | 
| Policy configuration unavailable | use cached policy or safe default                 | 
| Redis latency increases          | Fail fast and apply fallback                      |
| Network partition to store       | Use explicit availability policy                  |

### Example Design
- 
- Client
  - API Gateway
    - API leve coarse limit
  - Application Service
    - Authenticated user limit
    - Endpoint-specific limit
  - Redis Cluster
    - Atomic token-bucket script
    - Replication/failover
  - Business logic

- for payment requests : 
1. Check IP limit
2. Check user limit
3. Check payment-operation limit
4. If allowed, process request
5. If rejected, return 429 with Retry-After


### answer summary

- A strong concise answer :
```text
I would place a low-latency rate-limiting layer at the gateway or application boundary 
and use a shared Redis-based token bucket for globally consistent decisions. Each request
would generate a key based on the authenticated user, tenant, API ke, IP and endpoint as appropriate.
The decision and token update would execute atomically using a server-side script. The response
would include remaining quota and Retry-After when reject. I would use hierarchical limits
for global , tenant, user, and expensive endpoints. The design must define fail-open vs fail-closed behavior, 
protect against hot keys, monitor decision latency and rejection rates and distinguish rate limiting from concurrency limits and long-term quotas.
```



```text
A distributed rate limiter protects shared services by making request-admission decisions across
multiple application instances.Token buckets are useful for controlled bursts, while sliding-window
approaches provides more precise rolling limits. Shared state must be updated atomically,
usually though Redis or a similar low-latency store. A complete design must define
identity keys, hierarchical policies, 429 responses, fail-open or fail-clsed behavior, hot key
mitigation and observability
```