# Lesson 61 : System Design - URL Shortner

## Questions

1. What are the core functional requirements of a URL shortener ?
2. Why is the redirect path more read-heavy than the creation path ?
3. How would you estimate redirect traffic ?
4. What fields belong in a URL-mapping record?
5. Why is a unique constraint required for short codes?
6. Compare Base62 IDs, random tokens and hashes
7. Why should analytics be asynchronous?
8. What is a cache stampede?
9. How can hot keys affect the system?
10. How should cache failure be handled?
11. How can URL creation be made idempotent?
12. How should custom aliases be validated?
13. Which consistency requirements apply to URL mappings?
14. What should happen when a URL expires
15. How would you scale the database ?
16. How would you support multi-region traffic?
17. What abuse risks exist in a URL shortener?
18. Which metrics should be monitored?
19. What are the main trade-offs int the design?


## My summary

### Requirements

#### Functional requirements

- Create a short URL : `POST /v1/urls`
  - Request 
```json
{
  "originalUrl": "https://example.com/articles/distributed-systems",
  "expiresAt": "2027-01-01T00:00:00Z"
}
```
  - Response
```json
{
  "shortCode" : "aB31x",
  "shortUrl": "https://sho.rt/aB31x",
  "expiresAt": "2027-01-01T00:00:00Z"
}
```

- Redirect : GET /aB31x
  - Response
```http
302
Location : https://example.com/articles/distributed-systems
```

- Optional requirements
  - Custom aliases
  - URL expiration
  - Disable or delete a URL
  - Per-user ownership
  - Click analytics
  - Abuse detection
  - Rate limiting

#### Non-functional requirements

- Assume:
  - High availability
  - Low redirect latency 
  - Read-heavy workload
  - Durable URL mappings
  - Horizontal scalability
  - Eventual consistency for analytics
- example targets :
  - Redirect availability : 99.99%
  - Redirect p99 latency : below 100ms
  - Create latency : below 500 ms
- the redirect path is more latency-sensitive than the creation path

### Assumptions and scale estimates
- Assume
  - 100M stored URL mappings
  - 1M new URLs per day
  - 100M redirects per day
  - Read/write ratio : approximately 100 : 1
  - Peak traffic : 5x average traffic
- Redirect Traffic
  - `100,000,000 / 86,400 ~1,157 redirects / second average`
  - Peak : `1,157 x 5 ~ 5,785 redirects / second`
  - Design Target : `Approx 6,000 redirects / second`
- Creation Traffic
  - `1,000,000 / 86,400 ~ 12 creates / second average`
  - even with a significant peak factor, creation traffic is much lower than redirect traffic.
- This suggests : 
  - Optimize the read path heavily
  - Keep creation path durable and simple

### Core data model

| UrlMapping   |
|--------------|
| id           |
| short_code   |
| original_url |
| owner_id     |
| status       |
| created_at   |
| expires_at   |

- Possible statuses :
  - ACTIVE
  - DISABLED
  - EXPIRED
  - DELETED
- Important constraints :
```sql
UNIQUE(short_code)
```

- If custom aliases are supported :
```sql
UNIQUE(owner_id, custom_alias)
```

- Example relational schema : 
```sql 
CREATE TABLE url_mappings (
    id BIGINT PRIMARY KEY,
    short_code VARCHAR(20) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    owner_id BIGINT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NULL
    ); 
```
- Index :
```sql
CREATE UNIQUE INDEX ux_url_short_code
ON url_mappings(short_code); 
```

- The dominant query is :
```sql
SELECT original_url, status, expires_at
FROM url_mappings
WHERE short_code = ?;
```

### High-level architecture

- Client
  - Load Balancer
  - Redirect Service
    - Cache
    - URL Database
- Create requests : 
  - Client 
  - API Service
    - Cache population
  - URL population
- Analytics : 
  - Redirect Service
  - Event Queue
  - Analytics Workers
- Components

| Component         | Responsibility                |
|-------------------|-------------------------------|
| Load balancer     | Distribute traffic            |
| URL API service   | Create and manage mappings    |
| Redirect service  | Resolve short codes           |
| Cache             | Serve hot mappings quickly    |
| Database          | Durable source of truth       |
| Event queue       | Buffer click events           |
| Analytics workers | Process clicks asynchronously |

- For a smaller system, URL and redirect handling can initially be implemented in one service. They can be separated later if their scaling requirements diverge.

### Create URL flow

1. Client sends original URL
2. API validates the URL
3. API checks custom-alias availability if applicable
4. Service generates a unique short code
5. Mapping is inserted into the database
6. Mapping is added to the cache
7. Service returns the short URL

- Detailed flow : 
  - Client
  - API Service
    - Validates URL
    - Generate Code
    - Insert database row
    - Cache mapping
    - Return 201 Created
- Response
```http
201 Created
Location : /v1/urls/aB31x
```
```json
{
  "shortCode" : "aB31x",
  "shortURL" : "https://sho.rt/aB31x"
}
```

### Redirect flow

1. Client request short code
2. Redirect service checks cache
3. Cache hit returns redirect
4. Cache miss queries database
5. If active, populate cache
6. Return redirect
7. Publish analytics event asynchronously

- Client
- Redirect Service
  - Cache
    - Hit  --> 302 response
    - Miss --> Database
      - Cache
        - 302 response
- The analytics event should not delay the redirect :
  - Redirect response --> critical path
  - Analytics event   --> asynchronous path

### Cache Design

- Use the short code as the cache key :
  - url : aB31x
- Cached value
```json
{
  "originalUrl" : "https://example.com/articles/distributed-systems",
  "status" : "ACTIVE",
  "expiresAt" : "2027-01-01T00:00:00Z"
}
```
- Cache behavior
  - Cache hit : 
    - Return redirect immediately
  - Cache miss :
    - Query database
    - Validate status and expiration
    - Populate cache
    - Return redirect

- Possible cache settings
  - TTL              : 10 minutes to several hours
  - Eviction         : LRU or provider-specific
  - Negative caching : short TTL for missing codes
- Negative caching can protect the database from repeated requests for invalid codes : 
  - invalid-code -> NOT_FOUND for 30 seconds
- Use caution because a newly created code should not remain hidden behind a long negative-cache entry

### Cache consistency

- The database is the source of truth
- For creation :
  - Write database
  - Populate cache
  - Return success
- For disable / delete :
  - Update database
  - Evict cache entry
- Possible failure : 
  - Database update succeeds
  - Cache eviction fails
  - Old redirect remains temporarily available
- Mitigations :
  - Short cache TTL 
  - Retry eviction
  - Publish invalidation event
  - Use versioned cache entries
  - Use cache-aside reads
- For a security-sensitive disable operation, you may prefer a cache strategy that minimized the period during which a disabled URL remains active.

### Short-code generation

- The short code must be : 
  - Unique
  - Compact
  - URL-safe
  - Efficient to generate
  - Difficult to guess, if privacy matters
- Possible strategies : 
1. Database ID encoded in Base62
2. Random Base62 token
3. Hash of the original URL
4. Distributed ID generator
5. Custom alias

#### Base62 encoding

- uses : 
  - a-z
  - A-Z
  - 0-9
- Number of possible codes : 
  - Length 6 :
    - `62 ^ 6 ~ 56.8 billion`
  - Length 7 : 
    - `62 ^ 7 ~ 3.5 trillion`
- A numerical ID can be encoded into Base62
  - Database ID : 125000
  - Base62      : aB31x
- Advantages : 
  - Compact
  - Deterministic
  - No random collision during encoding
- Disadvantages :
  - Sequential IDs may be guessable
  - Requires a unique ID allocation strategy
  - May reveal approx creation order

#### Random tokens

- Generate a cryptographically secure random token :
  - aB31x
- Advantages
  - Harder to guess
  - Does not reveal count or order
  - Can be generated independently
- Disadvantages :
  - Collisions are possible
  - Requires uniqueness checks
  - Requires enough entropy
- Collision flow
  - Generate token
  - Attempt database insert
  - If unique constraint fails, retry
- Do not use predictable pseudo-random generation if URL privacy matters

#### Hashing the original URL

- A hash can be generated from :
  - hash(originalUrl)
- This is not always because : 
  - Same URL may ned multiple short links
  - Hash collisions must be handled
  - Changing expiration or ownership may require different mappings
  - Hash-derived codes may reveal duplicate relationships
- If deduplication identical URLs is requirement, use an expicit canonicalization and ownership policy rather than assuming hashing solves the entire problem.

### Custom aliases
  
- Allowing a custom alias : 
  - `POST /v1/urls`
```json
{
  "originalUrl" : "https://example.com",
  "customAlias" : "spring-course"
}
```

- Requires : 
  - Format validation
  - Reserved-word validation
  - Uniqueness constraint
  - Ownership rules
  - Abuse prevention
- Reserved aliases :
  - admin
  - login
  - api
  - health
  - actuator
  - favicon.ico
- A race-free uniqueness check requires a database constraint : 
  - UNIQUE(short_code)
- Do not rely only one : 
  - SELECT to check availability
  - INSERT later
- two concurrent request can both pass the check

### Redirect status code

- Possible redirect statuses : 
  - 301 Moved Permanently
  - 302 Found
  - 307 Temporary Redirect
  - 308 Permanent Redirect
- For a URL-shortening service, 302 or 307 is often safer when destination behavior may change.
- Consider :
  - 301 / 308 :
    - Clients and browsers may cache aggressively
  - 302 / 307 : 
    - More flexible for changing mappings
- The choice depends on whether the short URL is permanent bound to one destination
- Document the behavior and configure cache headers deliberately

### Database scaling 

- At 100 mappings, one indexed relational database may be sufficient depending on : 
  - Record size
  - Read traffic
  - Cache hit rate
  - Storage engine
  - Replication
  - Hardware
  - Query complexity
- Possible growth path :
  - Step 1 : 
    - Primary relational database
  - Step 2 : 
    - Read replicas and cache
  - Step 3 : 
    - Partition by short-code hash
  - Step 4 : 
    - Distributed key-value storage
- Avoid sharding before measurements show that a single database is insufficient

### Partitioning Strategy

- Partitioning becomes necessary, partition by :
  - hash(short_code)
- Example : 
  - hash(short_code) % 4
- Routes to :
  - Partition 0
  - Partition 1
  - Partition 2
  - Partition 3
- Hash partitioning distributes short codes relatively evenly
- Potential issues : 
  - Resharding complexity
  - Cross-partition administration
  - Hot keys
  - Global uniqueness
  - Backup and recovery
- A redirect request must calculate the partition before lookup

### Hot keys

- A viral URL may receive a large percentage of all traffic :
  - shortCode = viral123
- This creates a hot key
- Mitigations
  - Replicated cache entries
  - CDN caching 
  - Request coalescing
  - Local in-process cache
  - Load-aware routing
  - Prewarning
- Be careful with request coalescing : 
  - Many requests miss cache simultaneously
  - Only one request queries database
  - Others wait for the result
- This prevents cache stampede

### Cache stampede

- A cache stampede occurs when a popular entry expires and may requests query the database at the same time.
  - Popular entry expires
  - 10,000 requests miss cache
  - 10,000 database queries
- Mitigations :
  - Request coalescing
  - Early refresh
  - Randomized TTL
  - Stale-while revalidate
  - Distributed locking
  - Cache prewarning

- A shot URL service should protect its database from popular-key bursts.

### Analytics design

- Do not synchronously write analytics in redirect path : 
  - Redirect request
    - Lookup mapping
    - Write click database record
    - Return redirect
- This increases latency and couples redirects to analytics availabilty
- Prefer : 
  - Redirect Service
  - Click Event
  - Message Queue
  - Analytics Consumer
  - Analytics Store
```json
{
  "eventId" : "evt-123",
  "shortCode": "aB31x",
  "occurredAt" : "2026-09-27T12:00:00Z"
  "country" : "US",
  "userAgent" : "....",
  "referrer" : "..."
}
```

- Analytics can be : 
  - Eventually consistent
  - Sampled
  - Aggregated
  - Retained for a limited period
- Avoid storing unnecessary personal data


### Abuse and security
  
- A URL shortener can be abused for : 
  - Phishing
  - Malware distribution
  - Spam
  - Open redirect attacks
  - Illegal content
  - Traffic amplification
- Controls may include : 
  - Rate limiting
  - Destination validation
  - Malware and phishing scanning
  - Blocklists
  - Abuse reporting
  - Short-link expiration
  - User authentication
  - Domain restrictions
  - Audit logs
- Do not assume that HTTPS on the destination makes it safe
- Validate : 
  - Scheme
  - Hostname
  - Private network destinations
  - Loopback addresses
  - Encoded IP addresses
  - Redirect chains
- A server-side URL fetcher used for scanning must defend against SSRF

### Availability and failure handling

- Cache failure
  - Fallback 
    - Query Database
  - Protect the database with  :
    - Rate limiting 
    - Circuit breakers
    - Request coalescing
    - Read replicas
- Database failure
  - Possible behavior
    - Redirect cache hits continue
    - Cache misses return temporary failure
    - Creation requests fail safely
  - Do not redirect to an unverified or stale destination unless the busines requirements allow it
- Analytics queue failure
  - Possible behavior
    - Return redirect successfully
    - Buffer locally for a bounded period
    - Drop non-critical analytics
    - Retry asynchronously
  - the redirect path should not fail solely because analytics is unavailable

### Reliability and idempotency
  
- URL creation may be retried by the client
- If repeated creation should return the same short URL, support a client idempotency key :
```http
Idempotency-Key : client-request-123
```

- Store :
  - key
  - request hash
  - created short code
  - response
- For analytics events, use : 
  - eventId
- Consumers should deduplicate event IDs if duplicate click records are unacceptable
- Analytics often tolerates approx counts, but that must be an explicit product decision


### Multi-region design

- For global traffic : 
  - Users 
    - Region A
    - Region B
    - Region C
- Possible approaches : 
  - Single write region
    - All creation writes
      - Region A
    - Redirect reads
      - nearest region
    - Advantages : 
      - Simpler uniqueness
      - Simpler consistency
    - Disadvantages : 
      - Higher creation latency for distant users
      - Write-region failure affects creation
  - Multi-region writes :
    - Each region accepts creation
    - requires : 
      - Globally unique code generation
      - Conflict handling
      - Cross-region replication
      - Clear consistency model
  - For initial design, a single write region with globally replicated read data may be the simpler choice

### API design

- Create URL
```http
POST /v1/urls
Content-Type: application.json
Idempotency-Key: optional-key
```
  - Request
```json
{
  "originalUrl" : "https://example.com",
  "customAlias" : null,
  "expiresAt"   : null
}
```
  - Responses : 
    - 201 CREATED
    - 400 BAD REQUEST
    - 409 CONFLICT
    - 429 TOO MANY REQUESTS
- Get metadata
```http
GET /v1/urls/{shortCode}
```

  - Response : 
```json
{
  "shortCode" : "aB31x",
  "originalUrl" : "https://example.com",
  "status" : "ACTIVE",
  "createdAt" : "2026-09-27T12:00:00Z"
}
```

- Redirect
```http
GET /{shortCode}
```
  - Responses : 
    - 302 FOUND
    - 404 NOT FOUND
    - 410 GONE
  - Use 410 GONE when the short URL existed but has expired or been intentionally removed and that distinction is useful to clients

### Observability

- Track :
  - Redirect requests per second
  - Redirect success rate
  - Redirect p50/p95/p99 latency
  - Cache hit ratio
  - Database lookup latency
  - Invalid-code rate
  - Expired-link rate
  - Creation success rate
  - Collision retries
  - Queue depth
  - Analytics consumer lag
  - Hot-key traffic
- Useful logs :
  - requestId
  - shortCode
  - cacheHit
  - status
  - latencyMs
  - destinationDomain
- Do not log full URLs if they may contain sensitive query paramters
- Trace :
  - Client request
  - Redirect service
  - Cache
  - Database
  - Analytics publish

### Final architecture
                   Distributed    
                      Cache
- Client                 |
- DNS / Load Balancer    |
- URL Service ---------> |
  - Create API           |
  - Redirect API         |
  - Rate limiting        |
                         |
                       URL Database
                         |
                         |
                         v
                       Click Event
                         Queue
                         |
                         |
                         v
                      Analytics
                       Workers


### Trade-offs

- Relational database vs key-value store
  - Relational
    - Constraints and transactions
    - Easier administration initially
  - Key-value:
    - Excellent exact-key loop
    - Easier horizontal distribution
    - Fewer query capabilities
  - start with a relational database if :
    - The team already operates it
    - The scale is moderate
    - Strong uniqueness constraints are important
  - Move towards key-value or distributed storage when : 
    - Lookup volume exceeds database capacity
    - Access patterns remain simple
    - Horizontal partition becomes necessary
- Cache vs database-only
  - Cache :
    - Lower latency and database load
    - More invalidation complexity
  - Database-only
    - Simpler consistency
    - Higher read load and latency
- Synchronous vs asynchronous analytics
  - Synchronous
    - More accurate immediate result
    - Slower redirects 
    - Analytics outage affects redirects
  - Asynchronous
    - Fast redirects
    - Eventual analytics
    - Possible dropped or duplicated events

### answer summary
```text
I would build a read-heavy URL shortening service with a URL API, redirect service, durable URL-mapping datavase,
distributed cache and asynchronous analytics pipeline. URL creation
validates the destination, generates a unique Bas62 or random code,
stores it with a unique constraint, and populates the cache. 
Redirects check the cache first and fall back to the database,
while click analytics are published asynchronously so they do not affect redirect latency.
I would use rate limiting, cache-stampede, protection, idempotency for creation retries,
and monitoring cache hit ratio, tail latency, invalid links  and hot keys.
I would begin with a relational database and introduce read replicas or partitioning only when measurements show the need
```



```text
 URL shortener is a read heavy key-loop system. The design use durable URL
 mappings, unique short-code generation, a distributed cache for hot redirects, asynchronous
 analytics, rate limiting, cache-stampede protection, and explicit handling for expiration, 
duplication requests, abuse and database or cache failure
```