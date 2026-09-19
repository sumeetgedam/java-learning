# Lesson 29: Concurrent Collections

## Questions

1. Why is HashMap unsafe for concurrent updates?
2. What problem does ConcurrentHashMap solve?
3. Why is get-then-put unsafe?
4. What does merge do?
5. What does computeIfAbsent do?
6. What does putIdAbsent do?
7. What is weakly consistent iteration?
8. What is a BlockingQueue?
9. What is the producer-consumer pattern?
10. When would you use CopyOnWriteArrayList?
11. Why is CopyOnWriteArrayList unsuitable for frequent writes?
12. how does ConcurrentHashMap differ from synchronizedMap?

## My summary

- `ConcurrentHashMap` generally provides better scalability than a synchronized `HashMap` when many threads access the map.
- Simple HashMap
  - Lost updates
  - Inconsistent state
  - Race conditions
  - Possible structural corruption in unsafe usage patterns
- `Collections.synchronizedMap(new HashMap<>())`
  - provides synchronization
  - all access is coordinated through one shared lock
  - may reduce concurrency
- `ConcurrentHashMap`
  - supports concurrent reads and a large number of concurrent writes without one global exclusion lock
  - supports atomic-style operations such as computeIfAbsent and merge
- `computeIfAbsent`
  - Useful for initializing a value only when a key is missing
  - mapping function should be short and should not perform unrelated blocking work
- `compute`
  - Use compute when the new value depends on both the key and current value
  - returning `null` from a remapping function generally removes the mapping
- `putIfAbsent`
  - when you only want to insert if the key does not already exist
  - if key exists, value is nt replaced.
  - safe than check-then-put, which is not atomic
- `replace`
  - replace only when the key is already present
- Iteration behavior
  - weakly consistent
  - may proceed while other threads modify the collection
    - do not normally throw `ConcurrentModificationException`
    - may or may not reflect changes made after iteration begins
- `BlockingQueue`
  - supports producer-consumer workflow
  - Producer -> BlockingQueue -> Consumer
  - ArrayBlockingQueue
  - LinkedBlockingQueue
  - PriorityBlockingQueue
  - SynchronousQueue
  - DelayQueue
  - `put()` may wait if queue is full
  - `get()` may wait if the queue is empty()
- `CopyonWriteArrayList`
  - creates a new underlying array when modified
  - useful when Reads and iteration >> updates
  - Examples :
    - Listener lists
    - Configuration snapshots
    - Small collections that change rarely
  - poor choice when writes are frequent or list is large as each update copies the array


```text
Concurrent collections provide thread-safe access with specialized behavior
ConcurrentHashMap supports concurrent map operations, 
BlockingQueue coordinates producer and consumers,
and CopyOnWriteArrayList is useful when read greatly outnumber writes.
```