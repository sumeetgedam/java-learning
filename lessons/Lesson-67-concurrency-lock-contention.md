# Lesson 67 : Concurrency and Lock Contention

## My summary

### Shared mutable state
- Concurrent threads accessing the same variable can create race conditions
- Example : 
```java
int counter = 0;
//Thread A
counter++;
//Thread B
counter++;
```
- Without synchronization
  - Expected : 2
  - Actual   : 1 or 2
- The increment is not atomic :
  - Read counter
  - Increment
  - Write counter

### Synchronization with locks
- A lock ensures only one thread executes a critical section at a time.
```java
Object lock = new Object();
int counter = 0;
synchronized (lock){
    counter++;
}
```
- Guarantees : 
  - Memory visibility
  - Mutual exlcusion
  - Atomicity of operations inside the block
- A lock has costs : 
  - Lock acquisition time
  - Context switching
  - Cache invalidation
  - Contention delays

### Lock contention
- Lock contention occurs when multiple threads compete for the same lock
- Example : 
```java
synchronized(lock) {
    //citical section
}
```
- With low contention :
  - Thread typically acquires lock immediately
  - low latency
- With high contention : 
  - Thread waits for lock
  - Latency increases
  - Threads accumulate
  - Performance degrades
- Contention is often the dominant cost of locking, not the lock operation itself

### Reducing lock scope
- Minimize the time holding a lock
- Bad :
```java
synchronized (lock) {
    data = expensiveComputation();
    sharedList.add(data);
    moreExpensiveComputation();
}
```

- Better :
```java
Object result =  expensiveComputation();

synchronized (lock) {
    sharedList.add(result);
    moreExpensiveComputation();
}
```
- Hold the lock only for the minimum necessary operations

### Multiple locks and partitioning
- Instead of one lock protecting all data, use multiple locks : 
  - Lock A -> partition 1
  - Lock B -> partition 2
  - Lock C -> partition 3
- This reduces contention by allowing different threads to work on different partitions concurrently
- Example :
```java
class PartitionedCounter {
    private final int[] counters;
    private final Object[] locks;
    
    PartitionedCounter(int partitions) {
        counters = new int[partitions];
        locks = new Object[partitions];
        for(int i=0; i < partitions; i++) {
            locks[i] = new Object();
        }
    }
    
    void increment(int partition) {
        synchronized (locks[partition]) {
            counters[partition]++;
        }
    }
}
```
- Each partition can be updated concurrently

### False sharing
- False sharing occurs when two threads access different variables that happen to be on the same CPU cache line
- Example : 
  - Cache line (64 bytes)
    - counter_A (used by Thread A)
    - counter_B (used by Thread B)
  - When Thread A modifies counter_A, the entire cache line is invalidated.
  - Thread B must reload, even though it was only accessing counter_B
- This can significantly reduce performance without an obvious lock contention reason
- Mitigation :
  - Pad objects to avoid cache-line sharing
  - Use separate cache lines for thread-local data
  - Use thread-local variables
  - Use contention-reducing data structures
- Example padding : 
```java
class PaddedCounter {
    private volatile long value;
    private long p1, p2, p3; // padding
}
```
- Cache-line size is typically 64 bytes, padding ensures that each counter occupies its own cache line

### Volatile
- The volatile keyword ensure visibility of changes across threads without requiring a lock
  - private volatile boolean flag = false;
- Guarantees : 
  - Writes are immediately visible to other thread
  - No memory reordering of volatile operations
- Costs : 
  - More expensive than non-volatile reads/writes
  - Prevents some optimizations
  - Cache incalidation
- Use volatile for : 
  - Flags
  - Status variable
  - Shutdown signal
- Not appropriate for : 
  - High-frequency updates
  - Lock replacement
  - Complex synchronization

### Atomic operations
- Atomic classes provides lock-free thread-safe operations
- Example :

```java
AtomicInteger counter = new AtomicInteger(0);

counter.incrementAndGet();
counter.compareAndSet(0, 1);
counter.getAndAdd(5);
```
- Implement using : 
  - Compare-and-swap (CAS)
  - Atomic hardware instruction
- Advantages : 
  - No blocking 
  - No lock acquisition overhead
  - Higher performance under contention
- Limitations  
  - Complex multi operation sequences still need locks
  - Not all operations can be atomic
- For a simple counter with high update frequency , AtomicInteger may be better than synchronized

### Compare-And-Swap (CAS)
- CAS is an atomic hardware instruction : 
  - compare memory location to expected value
  - if equal, swap with new value
  - atomically return success or failure
- Pseudo-code : 
```java
if(memory == expected) {
    memory = new;
    return true;
}
return false;
```
- Use by atomic operations and lock-free algorithms.
- Retry loop :

```java
AtomicInteger ai = new AtomicInteger(0);
while(!ai.compareAndSet(oldValue, newValue)) {
    //retry
}
```
- CAS is efficient but can create contention if many threads retry simultaneously

### Lock-free programming
- A lock-free algorithm ensure progress even if some threads are delayed
- Example : 
```java
class LockFreeStack<T> {
    private AtomicReference<Node<T>> top =
            new AtomicReference<>();
    public void push(T value) {
        Node<T> node = new Node<>(value);
        Node<T> oldTop;
        do {
            oldTop = top.get();
            node.next = oldTop;
        }while(!top.compareAndSet(
                oldTop,
                node
        ));
    }
}
```
- Advantages
  - No blocking
  - No locking contention
  - Progress guaranteed
- Disadvantages : 
  - More complex code
  - CAS retry loops under contention
  - Not universally faster
- Lock-free programming is useful for :
  - High-frequency operations
  - Low contention scenario
  - published data structure
- Not always necessary for application-level concurrency

## ThreadLocal
- ThreadLocal provides per-thread storage : 
```java
ThreadLocal<Connection> connectionThreadLocal =
    ThreadLocal.withInitial(
            () -> createConnection()
    );
Connection conn = connectionThreadLocal.get();
```

- Uses  :   
  - Thread-specific resources
  - Avoiding lock contention for thread specific state
  - context information
- Risk : 
  - Memory leaks if not cleared
  - hidden state
  - ThreadLocal must be cleared in thread pool scenarios
- In a thread pool, always clear ThreadLocal
- otherwise the object persists across different tasks


### Executor and thread pools
- A thread pool reuses threads instead of creating new ones

```java
ExecutorService executor = Executor.newFixedThreadPool(10);
executor.submit(() -> {
    // work
});
```
- Advantages : 
  - reduced thread creaton overhead
  - bounded resrouce usage
  - task queuing
- considerations :
  - Queue size
  - rejection policy
  - thread lifecycle
- a bounded queue prevent unlimited task accumulation :

```java
ExecutorService executor =
        new ThreadPoolExecutor(
                4,
                8,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
```

### Thread coordination
- Thread may need to wait for events or each other

#### CountDownLatch

```java
CountDownLatch latch = 
    new CountDownLatch(3);
// Thread A
latch.countDown();

// thread B
latch.countDown();

// main thread
latch.wait(); // waits for 3 counts
```
#### CyclicBarrier

```java
CyclicBarrier barrier = new CyclicBarrier(3);
barrier.await(); // waits for ll threads
```

#### Semaphore

```java
Semaphore semaphore = new Semaphore(5);
semaphore.acquire(); // acquire permit
// critical section
semaphore.release();
```

### Deadlock 
- A deadlock occurs when threads holds locks and waits for locks held by other threads, craeting a cycle.
- Example : 
  - Thread A holds lock 1, waits for lock 2
  - Thread B holds lock 2, waits for lock 1
- Prevention
  - Acquire locks in a consistent order
  - Use timeouts
  - Use higher-level constructs
  - Avoid nested locks
- Better : 
```java
// Always acquire in order: A then B
synchronized(lockA) {
    synchronized(lockB) {
        // work
    }
}
```

### Measuring contention
- High lock contention manifests as  :
  - High thread context-switch rate
  - waiting threads in profiler
  - long lock hold time
  - reduced CPU utilization despite many threads
- Tools to detect contention : 
  - JFR recording
  - Thread dump analysis
  - Profiler lock profiling
  - jstack
  - GC log correlation with lock event

### Checkpoint
- How would you optimize heavily contented shared counter ?
  - First, I would measure the connection with JFR to confirm that contention is the bottleneck
  - If so, I would consider using AtomicInteger or AtomicLong instead of synchronized to reduce lock overhead
  - Partition the counter across multiple atomic variables so different thread update different partitions
  - or accepting approx counting if exact values are not required
  - I would then rerun the workload to measure improvement and verify
