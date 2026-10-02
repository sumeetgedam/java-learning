# Lesson 66 : Garbage Collection and Allocation

## My summary

### Generational hypothesis
- Most objects die young
- Observation : 
  - Object created
  - Most objects become unreachable quickly
  - Few objects survive long
- This suggested : 
  - Frequently collect the young generation
  - Barely collect the old generation
- The young generation is smaller, so collections are faster and create shorter pauses

### Heap layout
- A generational heap divides memory : 
  - Young generation
    - Eden space
    - Survivor 0
    - Survivor 1
  - Old generation
- Most allocation happen in Eden
- When Eden fills : 
  - Minor garbage collection
    - Surviving objects            --> Survivor space
    - Survivor objects with tenure --> Old generation


### Minor GC
- A minor GC collects the young generation
- Conceptually
1. Identify reachable objects from roots
2. Copy survivors to survivor space
3. Age objects
4. When tenure threshold is reached, promote to old generation
5. Reclaim unreachable memory
6. Resume application

- Duration : 
  - Typically 1-100 milliseconds
- Frequency depends on :
  - Allocation rate
  - Young generation size
  - Survival rate

### Major GC
- A major GC collects the old generation
- This is less frequent but can take much longer
  - Typically 100-1000+ milliseconds
  - Or much longer depending on heap size and collection strategy
- Long pauses can cause : 
  - Request timeouts
  - p99 latency spikes
  - Thread starvation
  - Application unresponsiveness

### Full GC
- A full Gc may collect both young and old generation and possibly compact the heap
- Full GC can be expensive and is generally undesirable
- Causes include : 
  - Explicit `System.gc()` call
  - Metaspace growth
  - Allocation failure
- Avoid relying on `System.gc()` in production

### Allocation rate and pressure
- suppose an application allocates : 
  - 1 GB per second of short-lived objects
- Even if the live set is 100MB, the CG must process 1GB per second
- High allocation causes : 
  - Frequent minor GC
  - Higher CPU consumption
  - More contention in memory subsystem
  - Increased pressure on CPU cache
- Measuring allocation : 
  - JFR recording
  - Profiler allocation tracking
  - GC log analysis
  - -XX:+PrintGCDetails

### Escape analysis
- Escape analysis determines whether an object's lifetime can be proven to be local to a method
- Example :
```java
public void doSomething() {
    List<String> list  =  new ArrayList<>();
    list.add("item");
    String result = list.get(0);
    return result;
}
```
- The `ArrayList` never escapes the method
- The JIT compiler may : 
  - Eliminate the allocation
  - Allocate on the stack
  - Use scalar replacement
- This optimization is called : 
  - Escape analysis -> scalar replacement
- Not all allocations can be eliminated through escape analysis, and the analysis itself has costs


### Object Pooling
- Object pooling reuses objects instead of creating new ones
- Example

```java
Pool<ByteBuffer> buffers = new Pool<>();

ByteBufffer buffer = buffers.acquire();
// use buffer
buffers.release(buffer);
```
- Advantages : 
  - Reduced allocation
  - Reduced GC pressure
  - Predictable allocation pattern
- Disadvantages : 
  - more complex coe
  - thread-safety requirements
  - Requires careful cleanup
  - Can hide bug
  - May use more memory
- Object pooling is useful for : 
  - Long-lived objects
  - Large objects
  - Frequently reused objects
  - Object created in tight loops
- Not useful for : 
  - Small short-lived objects that JVM can optimize
  - Most application-level objects
  - Objects with complex lifecycle

### Allocation friendly patterns
#### Avoid unnecessary boxing
- Bad : 
```java
List<Integer> numbers = new ArrayList<>();
for(int i = 0; i < 1000; i++) {
    numbers.add(i); // boxing
}
```
  - Better : 
```java
int[] numbers = new int[1000];
for(int i = 0; i < 1000; i++) {
    numbers[i] = i;
}
```
- Or use a primitive collection library

#### Avoid string concatenation in loops
- Bad : 
```java
String result = "";
for(String item : items) {
    result += item;
}
```

- Better :
```java
StringBuilder sb = new StringBuilder();
for(String item : items) {
    sb.append(item);
}
String result = sb.toString();
```

#### Avoid creating intermediate collections
- Bad : 
```java
List<User> adults = users.stream()
        .filter(u -> u.getAge() > 18)
        .collect(Collections.toList());
```

- Better, if you iterate once : 
```java
users.stream().filter(u -> u.getAge() > 18)
        .forEach(this::processUser);
```
### Reading GC logs
- Enable GC logging : 
  - `-Xlog:gc*:file=gc.log:time,level,tags`
- typical log line : 
  - [2026-09-20T12:00:00.123+0000][gc] GC(42) Pause Young (G1 Evacuation Pause) 45M -> 38M(256M) 12.345ms
- Important info  :
  - GC number: 42
  - Pause type: Young (G1 Evacuation Pause)
  - Before: 45M
  - After: 38M
  - Heap size: 256M
  - Duration: 12.345ms
- Monitor : 
  - Pause frequency
  - Pause duration (especially p99)
  - Heap utilization trend
  - Full GC occurrences
  - Metaspace growth
- Growth heap utilization over time may indicate a memory leak

### GC pause times and latency
- For latency sensitive systems, GC pauses directly affect tail latency
- Example : 
  - Normal request : 1ms
  - Request during major GC : 1ms + 500ms pause = 501ms 
- If 1% of request encounter a major GC pause :
  - p99 latency = 501ms
- Strategies to reduce GC impact : 
  - Keep heap size reasonable
  - Minimize allocation 
  - Time young generation size
  - Use a low-pause GC algorithm
  - Separate latency-sensitive workloads
- For some systems, sacrificing throughput for low pause times is the correct trade-off


### GC algorithm choices

- Different JVM implementation offer different collectors
- Serial GC
  - Single-threaded
  - low overhead
  - long pauses
  - Acceptable for small heaps or batch jobs
- Parallel GC
  - Multiple threads
  - Higher throughput
  - Moderate pauses
  - Good for server applications with reasonable latency tolerance
- CMS (Concurrent Mark Sweep)
  - Concurrent marking
  - Lower pause times
  - More fragmentation
  - Deprecated in recent JAva version
- G1GC (Garbage first)
  - Divides heap into region
  - Predictable pause times
  - Low-pause collection
  - Suitable for medium to large heaps
  - Default in recent Java versions
- ZGC, Shenandoah
  - Ultra-low pause time (milliseconds)
  - Concurrent collection
  - More CPU overhead
  - Suitable for latency-critical systems
- The right choice depends on 
  - Heap size
  - Latency requirements
  - Throughput goals
  - CPU availability


### Metaspace and class loading
- Metaspace stores lass metadata
  - Class definition
  - Method code
  - Constant pools
  - Annotations
- Metaspace issue
  - Class-loader leaks
  - Dynamic class generation
  - Framework overhead
- Monitor metaspace : 
  - `-XX:+PrintGCDetails` shows metaspace usage
  - JFR recording
  - JPS and jmap
- If metaspace grows unbounded, investigate
  - Class-loader lifecycle
  - Dynamic proxy creation
  - CGLIB usage
  - Framework plugin systems

### Memory leaks
- A memory leak in Java occurs when objects are no longer needed but remains reachable
- Common cause : 
  - Static collections that grow
  - Caches without eviction
  - Listeners not unregistered
  - Thread-local variables not cleared
  - Circular references preventing GC
  - Closed resources held by references
- Example : 
```java
static List<Data> cache = new ArrayList<>();
public void loadData(String id) {
    cache.add(new Data(id));
}
```
- Detecting leaks : 
  - Heap dump analysis
  - Heap trend over time
  - Metaspace growth
  - OutOfMemoryError investigation

### Heap dump analysis
- Generate with :
  - jmap -dump:live,format=b,file=heap.bin <pid>
  - jcmd <pid> GC.heap_dump heap.bin
- Analyze with :
  - Eclipse MAT
  - YourKit
  - JProfiler
  - JDK command-line tools
- Useful analysis : 
  - Largest objects
  - Class frequency
  - Reference chains
  - Duplicate strings
  - Memory by class
- A heap dump can be large and expensive to generate, so use only when necessary

### JVM tuning basics
- Common JVM options :

| JVM Option            | meaning               |
|-----------------------|-----------------------|
| -Xms                  | Initial heap size     |
| -Xmx                  | Maximum heap size     |
| -Xmn                  | Young generation size |
| -XX:MaxGCPauseMillis  | Target max pause time |
| -XX:+UseG1GC          | Select G1 collector   |
| -Xlog:gc*:file=gc.log | Enable GC logging     |

Example : 
```text
-Xms4G
-Xmx4G
-Xmn1G
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-Xlog:gc*:file=gc.log:time,level,tags
```    

### Allocation and latency trade-off
- Sometimes accepting more allocation simplifies code : 
```java
// Allocate a new list
List<String> filtered = items.stream()
                .filter(s -> s.startsWith("x"))
                .collect(Collectors.toList());
```

vs
```java
// no allocation but more complex
List<String> filtered = new ArrayList<>();
for(String item : items) {
    if(item.startsWith("x")){
        filtered.add(item);
    }
}
```

### Checkpoint
- How would you reduce GC pause times in a latency sensitive application ?
  - I would first measure the allocation rate and GC pause frequency with JFR and GC logs.
  - If dominant issue is young generation pressure, I would reduce allocation in hot paths or increase young generation size
  - If old generation collection pauses are the problem, I would consider a lower pause GC algorithm such as G1GC or ZGC keep heap size reasonable
  - Verify there are no memory leaks
  - I would then rerun the latency test to confirm that tail latency improves_