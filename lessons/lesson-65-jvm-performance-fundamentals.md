# Lesson 65 : JVM Performance Fundamentals

## My summary

### Performance mens more than speed
- Important performance dimensions include :
    - Latency      -> how long one operation takes
    - Throughput   -> how much work completes per second
    - Tail latency -> p95, p99, p99.9 response times
    - Allocation   -> how much memory is created
    - CPU usage    -> processor consumption
    - Memory usage -> heap and native memory pressure
- A change may improve one dimension while hurting another
- Example : 
  - Batching :
    -  improving throughput
    - may increase individual request latency

### The optimization process
- Use this sequence : 
1. Define the performance goal
2. Measure current behavior
3. Identify the bottleneck
4. Change one thing
5. Measure again
6. Check correctness and trade-offs

- Avoid :
  - Use fewer objects because object allocation is slow
- Better
  - Profiling shows that allocation rate causes frequent young-generation garbage collections, so I will reduce temporary object creation and measure p99 latency again

### Main JVM performance areas
#### CPU-bound workload
- The application spends most of its time computing
  - Example : 
    - Compression
    - Encryption
    - Parsing
    - Large calculations
  - Possible optimizations
    - Better algorithm
    - Reduce unnecessary work
    - Parallelize safely
    - Improve data locality
    - Use efficient primitive representations

#### I/O-bound workload
- The application spends most of its time waiting
  - Example : 
    - Database calls
    - Network requests
    - Disk access 
    - External APIs
  - Possible optimizations
    - Connection pooling
    - Caching
    - Batching
    - Asynchronous processing
    - Reducing network calls
- Adding more CPU does not necessarily fix the I/O bottlenexk


### JVM memory areas
- At a high level : 
  - Heap : 
    - Objects and arrays
  - Thread stack : 
    - Method frames and local variables
  - Metaspace : 
    - Class metadata
  - Code cache : 
    - JIT-complied native code
  - Native memory : 
    - JVM internals, direct buffers, libraries

#### Heap
- Most Java objects are allocated on the heap
```java Order order = new Order();```
- Heap pressure can cause : 
  - more garbage collection
  - longer pauses
  - higher CPU usage
  - OutOfMemoryError

#### Thread stack 
- Each thread has its own stack  :
  - Thread A -> Stack A
  - Thread B -> stack B
- Too many threads consume native memory and can cause scheduling overhead

#### Metaspace
- Stores class metadata
- Dynamic class generation or class loader leaks can cause metaspace growth

### Allocation rate
- Allocation rate means how much memory the application creates over time
  - Application creates 2GB of short-lived objects per second
- Even if the live object set is small, the garbage collector must process those objects
-High allocation can come from
  - Temporary collections
  - String concatenation
  - Serialization
  - Boxing
  - Streams in hot loops
  - Short-lived DTOs
  - Repeated parsing
- The correct response is not always "eliminate all allocations"
- Allocation may improve clarity and maintainability
- Optimize only when measurement shows it matters

### Garbage collection and latency
- Garbage collection reclaims memory from objects that are no longer reachable
- Simplified lifecycle : 
  - Object created
  - Object used
  - Object becomes unreachable
  - Garbage collector reclaims memory
- GC can consume : 
  - CPU time
  - Memory bandwidth
  - Application pause time
- For latency-sensitive systems, tail latency is especially important : 
  - Average latency : 2 ms
  - p99 latency     : 200ms
- The average looks good, but one percent of requests experience much higher latency

### JIT compilation
- The JVM initially interprets bytecode and can later compile frequently executed code into optimized native machine code
- Conceptually : 
  - Java source
  - ByteCode
  - Interpreter
  - Hot methods detected
  - JIT compilation
  - Optimized machine code
- The JVM may optimize based on observed runtime behavior
- Possible optimization include : 
  - Method inline
  - Dead-code elimination
  - Loop optimization
  - Escape analysis
  - Devirtualization
- This means benchmark result can change during warm-up

### Warm-up matters
- A benchmark may show different results at different stage :
  - Cold start : 
    - class loading
    - interpretation
    - initialization
  - Warm state  :
    - JIT-compiled hot methods
    - optimized execution
- Do not conclude that one implementation is better fomr a few calls
- Use : 
  - WArm-up iterations
  - Multiple measurement iterations
  - Stable input
  - JVM for microbenchmarks
- Avoid measuring with  : 
  - `System.currentTimeMillis();`

### CPU cache and data locality
- Modern CPUs have cache layers : 
  - CPU registers
  - L1 cache
  - L2 cache
  - L3 cache
  - Main memory
- Accessing data closer to the CPU is generally faster
- Performance can improve when data is : 
  - Compact
  - Sequential
  - Predictable
  - Reused
- Poor locality can result from : 
  - Random pointer chasing
  - Large object graphs
  - Scattered memory access


### A practical profiling mindset
- When an application is low, ask : 
  - is it CPU-bound ?
  - is it waiting on I/O?
  - is it allocation too much?
  - is it blocked on locks?
  - is GC consuming time?
  - is the database slow?
  - is the queue saturated?
- Useful evidence includes : 
  - CPU profiles
  - Allocation profiles
  - GC logs
  - Thread dumps
  - Heap dumps
  - Latency histogram
  - Database timing
  - JFR recordings

### Checkpoint
- How would you investiagte slow Java service
  - I would first define whether the problem is throughput, average latency or tail latency
  - Then I would measure CPU , allocation rate, garbage-collection, activity, thread states, lock contention, database latency, and downstream calls using tools such as JFR, profilers, GC logs, and metrics.
  - I would identify the dominant bottleneck, make one targeted change, rerun the workload and verify both performance and correctness
- 