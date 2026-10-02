# Lesson 68: Low-latency Java Design

## My summary

### Latency vs throughput
- these are often competing goals
- Throughput optimization
  - Process as much work per second as possible
  - May batch work
  - May increase individual request latency
  - Acceptable latency variation
- Latency optimization
  - Complete each request as fast as possible
  - Minimize tail latency 
  - Predictable response time
  - Often sacrifices some throughput
- Example : 
  - Batching 100 requests together : 
    - improves throughput
    - increases batch-tail latency
- A low-latency system prioritizes the latency requirement over throughput

### Sources of latency

- Computation
  - Algorithm complexity
  - Memory access
  - CPU cache misses
- Allocation and GC
  - Object creation
  - Garbage collection pauses
  - Memory pressure
- Locks and contention
  - Lock acquisition
  - Lock hold time
  - Context switching
- I/O
  - Network calls
  - Disk access
  - database queries
- Scheduling
  - OS scheduling design
  - Thread preemption
  - Context switches
  - JVM internal operations

### Predictability
- Low latency systems must be predictable
- Bad latency profile : 
  - Average : 2ms
  - p50     : 2ms
  - p99     : 500ms
  - p99.9   : 2000ms
- Better latency profile : 
  - Average : 5ms
  - p50     : 5ms
  - p99     : 6ms
  - p99.9   : 7ms
- Higher average, but far more predictable
- Sources of unpredictability
  - Garbage collection pauses
  - Lock contention
  - Scheduling delay
  - System events
  - Swapping

### Eliminate GC pauses
- For ultra-low latency, minimizes or eliminate GC pauses
- Possible approaches : 
  - Reduce allocation : 
    - Reuse objects
    - Preallocate
    - Avoid temporary object
  - Tune GC
    - Use a low pause collector(ZGC, Shenandoah)
    - Increase heap size
    - Tune young-generation size
  - Avoid GC
    - Fixed heap size
    - Preallocate all objects
    - Never allocate in the hot path
    - Use off-heap memory
- Example preallocate object pool : 
```java
class NoGcringBuffer {
    private final Event[] events;
    private int index = 0;
    NoGcringBuffer(int size) {
        events = new Event[size];
        for(int i = 0; i <size; i++) {
            events[i] = new Event();
        }
    }
    Event nextEvent() {
        Event e = events[index];
        index = (index + 1) % events.length;
        return e;
    }
}
```
- All objects are allocated upfront, the hot path reuses existing objects

### Mechanical sympathy
- "Mechanical sympathy" means designing software to work with the hardware , not against it
- Key hardware properties : 
  - CPU cache hierarchy
  - Memory bandwidth
  - Pipeline efficiency
  - Branch prediction
  - Prefetching behavior
- Example : data layout
- Bad : 
```java
class Item {
    long id;
    long timestamp;
    // .... other fields scattered
}
class Node {
    Item item;
    Node next;
}
```
- Traversing a linked list involves pointer chasing, which has poor cache locality
- Better
```java
class ItemArray {
    long[] ids;
    long[] timestamps;
    // ... arrays of primitives
}
```
- Iterating an array is cache-friendly because data is contiguous

### CPU cache and prefetching
- Modern CPUs prefetching data they predict you will need
- Predictable access pattern : 
  - Sequential memory access
  - Regular strides
  - Consistent branch pattern
- Unpredictable : 
  - Random memory access
  - pointer chasing
  - mispredicted branches
- Design data structures for cache friendliness : 
  - Sequential arrays
  - compact objects
  - minimize pointer indirection
  - align data on cache-line boundaries

### Latency-optimized architectures

- Single-threaded processing
  - Avoid locks and context switches entirely : 
    - Single thread process all requests
    - requests queued externally 
    - threads never waits
  - Advantages
    - No lock contention
    - no context switching
    - predictable
  - Disadvantages
    - cannot scale beyond one core
    - requires external queuing
- Pin threads to CPU code
  - Bind threads to specific CPU cores :
    - Thread A -> CPU core 0
    - Thread B -> CPU core 1
  - Reduces context switching and cache thrashing
- Isolate threads
  - use thread affinity and scheduling priority
    - Critical thread -> dedicated core
    - no other work on that core
  - this requires cooperation with the operating system

### The Disruptor pattern
- the Disruptor is a ring-buffer based message-passing pattern designed for ultra-low latency
  - Producer
  - Ring buffer (preallocated)
  - event handler
- key features : 
  - preallocated ring buffer
  - Lock-free coordination
  - mechanical sympathy
  - cache-line alignment
  - single-producer or multi-producer modes
- Example use :

```java
RingBuffer<Event> buffer =
        RingBuffer.createSingleProducer(
                Event::new,
                1024
        );
buffer.publishEvent((event, sequence) ->{
    event.data = input;
});
EventHandler<Event> handler =
        (event, sequence, endOfBatch) -> {
            process(event);
        };
Disruptor<Event> disruptor =
        new Disruptor<>(
                Event::new,
                1024,
                Executors.newFixedThreadPool(1)
    );
disruptor.handleEventsWith(handler);
```
- the disruptor is useful for extreme low-latency scenarios but adds complexity

### Batching and pipelining
- Batching can improve throughput
  - Process 100 items together
  - Better CPU cache utilization
  - amortized overhead
- but it increases latency for the batch tails : 
  - Item 1   : low latency
  - Item 100 : high latency
- Pipelining stages can process work in parallel: 
  - Stage A -> Stage B -> Stage C
- This overlaps computation, improving throughput and sometimes reducing tail latency if stages are well-balanced

### Backpressure for latency
- Backpressure protects against queue buildup
  - Bounded queue
  - Reject or wait when full
- This prevents unlimited latency increases from queue accumulation

### Measuring tail latency
- Collect all request latency, not just average
- use percentile
  - p50 (median)
  - p95
  - p99
  - p99.9
  - p99.99
  - max
- Tools :
  - HdrHistogram
  - JMH
  - JFR
  - Custom instrumentation

### Production consideration

- Monitoring 
  - Track : 
    - p50, p95, p99, p99.9 latency
    - Allocation rate
    - GC pause frequence
    - Context switch rate
    - Lock contention
    - Thread scheduling delays
  - Logging
    - avoid logging in latency-critical paths
  - Testing
    - test under production like load
      - full gc cycles
      - real network latency
      - real database latency
      - peak traffic
    - deployment
      - single threaded low latency components may not scale horizontally,
      - Plan : 
        - hardware scaling
        - network isolation 
        - load balancing strategy
        - failover behavior


### Trade-offs
- Low-latency design often trades
  - throughput for latency
  - simplicity for complexity
  - portability for hardware optimization
  - flexibility for predictability
  - Garbage-collected languages for C++
- example trade-off : 
  - general-purpose JVM tuning : 
    - balanced latency and throughput
    - simpler operations
    - more portable
  - ultra-low-latency tuning : 
    - minimizes p99 latency
    - sacrifices average throughput
    - requires specialized deployment
    - less portable

### Checkpoint
- How would you design a low-latency trading system in java?
  - I would minimize garbage collection  by preallocating objects and using fixed size ring buffer
  - I would pin critical threads to dedicated CPU cores and eliminate locks in the hot path by using a single threaded event loop with a lock-free ring buffer such as disruptor
  - I would measure tail latency percentile with HdrHistogram and use JFR to identify scheduling delays and unexpected pauses.
  - I would structure data for CPU cache friendliness, avoid dynamic allocation in the critical path, and use mechanical sympathy principles.
  - I would test under production-like conditions with real network and database latency to validate the design