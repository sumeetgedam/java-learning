# Lesson 52: Spring Caching

## Questions

1. Why is caching useful?
2. What does Spring's cache abstraction provide?
3. Does Spring's cache abstraction provide the actual cache store?
4. What does @EnableCaching do?
5. What does @Cacheable do?
6. What does @CachePut do?
7. What does @CacheEvict do?
8. What is the difference between condition and unless?
9. Why must cache keys include ever input that affects a result?
10. What is cache invalidation?
11. Why can caching search result be difficult?
12. What is TTL?
13. What is negative caching?
14. Why is tenant-aware cache-key design important?
15. Why can self-invocation bypass caching?
16. What is the difference between a local cache and a distributed cache?
17. Why are cache operations not automatically part of a database transactions?
18. What cache metrics should be monitored
19. Why does a high cache hit rate not guarantee correctness?
20. When should direct CacheManager access be preferred over annotations?


## My summary

- Spring's cache abstraction adds caching behavior to methods without coupling application code to one specific cache implementation
- It defines common annotations and APIs, while the actual storage is provided by a cache provider
- Caching can reduce:
  - Database load
  - External API calls
  - Response latency
  - CPU-intensive calculations
- Caching is not automatically beneficial, it introduces concerns such as stale data, memory usage, invalidation, and consistency
- Spring provides common abstractions
  - Cache
  - CacheManager
  - CacheResolver
- The abstraction does not itself provide a complete cache store.
- A provider or implementation must back it, such as:
  - Simple in-memory cache
  - Caffeine
  - Redis
  - JCache
  - Hazelcast
  - Infinispan
- `@EnableCaching`
  - activates annotation-driven cache behavior
  - Avoid putting @EnableCaching directly on the main application class when caching should not be mandatory for every environment or test suite.
- `@Cacheable`
  - use when a method result should be stored and reused
  - Behavior
    - First call : 
      - execute method
      - store result under cache key
    - Later call with same key
      - return cached result
      - skip method execution
  - By default, Spring uses method arguments to help create the cache key
  - Cache keys
    - Method argument determines the key
    - Different IDs produces different entries
    - we can also specify a key explicitly
  - use `unless` to prevent a result from being cached
    - unless is evaluated after the method returns
  - use `condition` to decide whether caching should apply before method execution
    - evaluated before invocation
- `@CachePut`
  - always executes the method but stores the returned result in the cache.
  - Cacheable may skip method execution on cache hit
- `@CacheEvict`
  - use when cached data is no longer valid
  - Run eviction after method completes successfully by default
  - use `beforeInvocation = true` to evict before invocation
  - Cache invalidation becomes harder as the number of representation increases.
- `@Caching`
  - use to combine several cache operations
  - this can update the individual entity cache while invalidating broader search results.
- If no external cache provider is present, spring boot ay consider a simple in-memory cache
  - `spring.cache.type=simple`
  - Per-process only
  - No sharing across application instances
  - Data lost on restart
  - Limited eviction controls
- Caffeine is a high-performance local in-memory cache
  - spring.cache.type=caffeine
  - spring.cache.cache-names=users,userSearch
  - spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=10m
- Redis is useful when multiple instances need to share cached data
  - A redis cache requires
    - Redis dependency
    - Redis connection configuration
    - Serialization decisions
    - TTL configuration
    - Network and authentication security
- TTL
  - provider specific
  - Short TTL:
    - exchange rates 
    - search results
    - temporary availability
  - Longer TTL:
    - country lists
    - product catalog metadata
    - rarely changed configuration
  - not a substitute of invalidation
- Cache invalidation
  - common strategies include
    - Cache-aside
      - Read:
        - check cache
        - if missing, load database
        - put result in cache
      - Write
        - update database
        - evict or update cache
      - @Cacheable commonly supports the read side of this pattern
    - Write-through
      - Write cache
      - Cache writes database
    - Write-behind
      - Write cache
      - Database update happens later
    - Refresh-ahead
      - Before entry expires
        - refresh value proactively
- Cache metrics
  - Manycache provider expose metrics such as:
      - Hit count
      - Miss count
      - Eviction count
      - Load time
      - Entry count
  - Monitor
    - Hit ratio
    - Miss rate
    - Eviction rate
    - Cache size
    - Memory usage
    - Latency
  - A cache with a low hit rat may add complexity without providing meaningful benefit


```text
Spring caching applies cache behavior through proxies and annotations
Cacheable reuses results, CachePut refreshes entries after method execution, and CacheEvict removes stale entries.
Correct key design, invalidation, TTLs, tenant isolation, and consistency are more important than simply enabling cache
```