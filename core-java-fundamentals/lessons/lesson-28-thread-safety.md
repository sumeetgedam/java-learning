#

##

## My summary
    
- Code is thread-safe when it behaves correctly when accessed by multiple threads concurrently
  - Race condition
    - multiple threads update shared state incorrectly
  - Visibility problem
    - one thread updates value but another thread does not immediately observe the update
- `synchronized`
  - Mutual exclusion : only one thread enters the protected section at a time.
  - Visibility : changes become visible to another thread acquiring the same monitor.
- `volatile` guarantees visibility of a variable between threads
- Atomic classes provide thread-safe operations on individual values without manually using `synchronized`
  - useful for counters , flags, sequence numbers, and other simple shared variables
- `ReentrantLock` provides capabilities beyond basic `synchronized`
  - Explicit local/unlock
  - `tryLock()`
  - Interruptible lock acquisition
  - Optional fairness configuration
  - always unlock in `finally` , otherwise an exception can leave the lock permanently held.
- `tryLock()` attempts to acquire lock without waiting indefinitely
  - useful when the thread should perform another action instead of waiting forever.

| Requirement                                  | Suitable tool                 |
|----------------------------------------------|-------------------------------| 
| Simple critical section                      | `synchronized`                | 
| Visibility of a simple flag                  | `volatile`                    | 
| Atomic counter or single value               | `AtomicInteger`, `AtomicLong` |
| Timed lock aquisition                        | `ReentrantLock`               |
| Multiple conditions or advanced lock control | `Lock` and `Condition`        |
| Shared concurrent map                        | `ConcurrentHashMap`           |

- Do not use `volatile` as a replacement for synchronization when multiple operations must be treated as one atomic action

```text
synchronized provides mutual exclusion and visibility,
volatile provides visibility but not compound-operation atomicity,
atomic classes provide lock-free operations for individual values, and ReentrantLock provides more explicit locking control.
```