# Lesson 32: Garbage Collection and JVM Monitoring

## Questions

1. What makes an object eligible for garbage collection?
2. Why does eligible for collection not mean collected immediately?
3. What is the generational hypothesis?
4. What are young and old generations?
5. What is a stop-the-world pause?
6. What is the primary goal of the Parallel collector?
7. What is G1 designed to balance?
8. When might a low-pause collector be appropriate?
9. What does -Xmx control?
10. What does -Xlog:gc do?
11. What is jcmd used for?
12. What is jstat used for?
13. What is a heap dump?
14. What can cause frequent Full GC events?
15. Why should performance changes be measured rather than assumed?

## My summary

- Garbage collection reclaim heap memory occupied by objects that are no longer reachable by the application.
- An object is eligible for garbage collection when it no longer be reached from active references such as :
  - Local variables
  - Static fields
  - Live Threads
  - JNI references
  - Other reachable objects
- The JVM decides when collection should occur.
- Requirement :
  - Without garbage collection, applications would need to manually release heap objects
    - allocate object
    - use object
    - manually release object
  - Java instead tracks object reachability :
    - allocate object
    - use object
    - object becomes unreachable
    - GC eventually reclaims memory
  - Garbage collection does not automatically close external resources such as :
    - Files
    - Sockets
    - Database connections
    - HTTP connections
  - Use try-with-resources or explicit resource-management APIs for those resources.
- Most garage collectors use the observation that :
  - Most objects die young, while objects that survive longer are more likely to remain alive.
  - This leads to a generational organization : 
    - Heap
      - Young generation
        - Eden
        - Survivor area
      - Old generation
  - Typical Object flow
    - new object
    - Eden
    - Survivor area 
    - Old generation
- Young generation collection
  - Reclaims objects in the young generation
  - Frequent
  - Relatively short
  - Handles many short-lived objects
- Old generation collection
  - Reclaims older objects
  - Less frequent
  - Potentially more expensive
  - May involve more live data
- `Full GC`
  - A full collection attempts to process the entire heap and may cause a long pause.
  - Frequent full GCs can indicate :
    - Heap pressure
    - High allocation rates
    - Too many long-lived objects
    - Large or humongous allocations
    - Inadequate collector progress
- `Stop-the-world` pauses
  - During this phase , application threads are paused while JVM performs required GC work
  - Application threads : running
  - Stop-the-world pause
  - GC work
  - Application threads: resume
- A collector may perform some work concurrently with application threads, but most collectors still have stop-the-world phases.
- Performance is usually evaluated using several metrics : 
  - Throughput : useful application work / total runtime
  - Pause time : duration application threads are stopped
  - Latency : time required to respond
  - Allocation rate : rate at which objects are created.
  - (Improving one metric may negatively affect another)
- Common HostSpot Collectors
  - Serial collector
    - Uses a single thread for garbage collection
    - `java -XX:+UseSerialGC ...`
    - small heaps
    - Single-processor environments
    - Simple applications
  - Parallel collectors
    - Uses multiple GC threads and focuses primarily on throughput
    - `java -XX:+UseParallelGC`
    - Useful when maximum application throughput is more important than very short pauses.
  - G1 collector
    - G1 is a mostly concurrent, region-based collector designed to balance throughput and pause-time goals
    - It is the default collector on most server-class configurations in HotSpot
    - `java -XX:+UseG1GC ...`
    - it divides the heap into regions rather than requiring one contiguous young or old
    - commonly suitable for
      - Medium to large heaps
      - Applications with pause-time goals
      - Workloads with changing allocation patterns
  - ZGC
    - ZGC is designed for very low pause times, including large heaps
    - At the cost of additional concurrent work and trade-offs that must be measured for the application
- Collector selection

| Primary goal                            | starting point             |
|-----------------------------------------|----------------------------| 
| Small application or simple environment | Serial                     |
| Maximum throughput                      | Parallel                   | 
| Balanced latency and throughput         | G1                         | 
| Very low pause times                    | ZGC                        |
| Unkown workload                         | JVM defaults, then measure |

- HotSpots ergonomics choose defaults based on factors such as available processors, heap size, and environment.
- GC logging
  - `java -Xlog:gc*:file=gc.log:time,uptime,level,tags -cp "target\classes com.learning.core.jvm.GcDemo`
  - `java -Xlog:gc -cp "target\classes com.learning.core.jvm.GcDemo`
  - `java -Xlog:gc*=debug -cp "target\classes com.learning.core.jvm.GcDemo`
- Heap Settings
  - `-Xms` : initial heap size
  - `-Xmx` : maximum heap size
  - for more predictable memory reservation, some applications set both to the same value
  - Do not choose heap values blindly, a larger heap may reduce collection frequency but can increase memory usage and potentially increase the amount of work required during collection
- `java -Xms64m -Xmx128m -Xlog:gc -cp "core-java-fundamentals\target\classes" com.learning.core.jcm.GcDemo`
- `jcmd`
  - sends diagnostic commands to a running JVM.
  - It can list JVM processes and request information such as thread dumps, VM flags, and heap information
  - jcmd <pid> VM.version
  - jcmd <pid> VM.command_line
  - jcmd <pid> VM.flags
  - jcmd <pid> GC.heap_info
- `jstat`
  - reports JVM performance and resource information, including garbage collection statistics
  - It is useful for observing heap sizing and GC behavior over time.
  - jstat -gc <pid> 1000 : samples GC-related information every 1000 milliseconds
  - jstat -class <pid> 1000
  - jstat -gccapacity <pid> 1000
  - jstat -gcutil <pid> 1000
- Heap dumps :
  - A heap dump captures information about objects in the heap
  - using jcmd
    - jcmd <pid> GC.heap_dump heap.hprof
  - hprof file can be analyzed with a heap-analysis tool such as Eclipse memory analyzer
  - configure heap dump when an out-out-memory occurs :
    - `java -XX:+HeapDumpOnOutOfMemmoryError -XX:HeapDumpPath=./dumps -cp "target/classes" com.learning.core.jvm.GcDemo`
- Thread diagnostics
  - `jcmd <pid> Thread.print -l`
  - `BLOCKED` threads
  - `WAITING` threads
  - Deadlock information
  - Repeated stack traces
  - Threads waiting on the same monitor
- Basic troubleshooting
  - Symptom --> Measure --> Collect evidence --> Form a hypothesis --> Change one thing --> Measure again
  - High GC frequency
    - Allocation rate
    - Temporary object creation
    - Heap Size
    - Object retention
    - Batch sizes
  - Long pauses
    - GC log pause durations
    - Number of live objects
    - Collector choice
    - Large allocations Full GC events
  - Memory Leak suspicion
    - Heap growth over time
    - Heap dump
    - Static collections
    - Caches without eviction
    - Listener registrations
    - Thread-local values
    - Class-loader retention
  - High CPU
    - GC thread activity
    - Application threads
    - Lock contention
    - Busy loops
    - Excessive allocation

```text
Garbage collection reclaims unreachable heap objects.
Different collectors optimize for different balances between throughput, pause time and latency
GC logs, jcmd, jstat, thread dumps, and heap dumps provide evidence for diagnosing JVM behavior.
```