# Lesson 30: Liveness Problem and Coordination

## Questions

1. What is a deadlock?
2. How can lock ordering prevent deadlocks?
3. What is a livelock?
4. What is starvation?
5. How can timeouts improve liveness?
6. What is CountDownLatch used for?
7. Why is CountDownLatch one-shot?
8. What is semaphore used for?
9. What is CyclicBarrier used for?
10. What is the difference between CountDownLatch and CyclicBarrier?
11. Why should lock scope be small?
12. When might a fair lock be useful?

## My summary

- A deadlock occurs when threads wait forever for locks held by each other.
  - Preventing
    - Use consistent lock ordering, 
      - threads should acquire locks in the same order.
    - Never acquire them in the reverse order elsewhere
    - Keep critical sections small, 
      - avoid slow operations while holding a lock
    - User timed lock acquisition
      - Timeouts allow the code to recover instead of waiting forever.
- In a livelock, threads are active but make no useful progress.
  - unlike deadlock, threads are not blocked, they are repeatedly reacting to each other
  - Prevention
    - Add randomized backoff
    - Limit retries
    - Use a coordinator
    - Avoid symmetric retry behavior
- Starvation occurs when a thread cannot regularly obtain access to a shared resource because other threads continuously take priority
  - Possible causes
    - a long held lock
    - excessive high priority work
    - unfair scheduling
    - one thread repeatedly reacquiring a resource
  - Possible mitigations
    - keep lock duration short
    - use fair locks where appropriate
    - avoid unlimited retry loops
    - bounds queue sizes
    - use timeouts
- A `CountDownLatch` allows one or more threads to wait until a count reaches zero.
  - one shot , cannot be reset
- A `Semaphore` controls access using permits.
  - A thread must acquire a permit before entering and release it afterward.
  - Use cases
    - Connection pools
    - Rate limited resources
    - Limiting concurrent requests
- A `CyclicBarrier` allows a fixed number of threads to wait until all participants reach the same point.
  - when all threads arrive, they continue.
  - unlike `CountDownLatch`, a `CyclicBarrier` ca be reused for multiple rounds

```text
Deadlock means threads wait forever, 
livelock means threads remain active without progress,
and starvation means a thread cannot obtains a resource regularly.
Consistent lock ordering, short critical sections, timeouts and coordination utilities help improve liveness.
```