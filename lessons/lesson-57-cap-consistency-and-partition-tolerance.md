# Lesson 57: CAP, Consistency, and Partition Tolerance

## Questions

1. What does CAP stand for?
2. What does consistency mean in the CAP theorem?
3. What does availability mean in the CAP theorem?
4. What is a network partition?
5. Why is "pick any two" an incomplete explanation?
6. Why is partition tolerance generally required in distributed systems?
7. What is a CP system?
8. What is an AP system?
9. What is strong consistency?
10. What is eventual consistency?
11. What is read-your-writes consistency?
12. What are monotonic reads?
13. What is linearizability?
14. What is the difference between serializability and linearizability?
15. Why can one application use difference consistency models for different data?
16. Why does string consistency often increase latency?
17. Why do AP systems need conflict-resolution strategies?
18. Why does CAP not directly select a database for you?
19. What is the difference  between a network partition and data partitioning?
20. How would you choose consistency for inventory versus analytics?

## My summary

- The CAP theorem concerns what a distributed data system can guarantee when a network partition occurs.
- The three CAP properties
  - C -> Consistency
    - Consistency meas that every successful read receives the most recent successful write, or an error
    - Example : 
      - Client writes : 
        - balances = 100
      - Subsequent read :
        - balance = 100
    - A stale value such as 80 would violate this strong consistency definition
    - This is stronger than simply saying : 
      - All replicas eventually contains the same value
  - A -> Availability
    - Availability means that every request received by a non-failing node eventually receive a valid response
    - The response may not contain the newest value, depending on the system's design
      - Request arrives at a reachable node
      - Node returns a response
    - A system that refuses all requests during a partition may preserve consistency but sacrifice availability.
  - P -> Partition tolerance
    - A network partition occurs when nodes cannot reliably communicate with one another.
    - Node A x Node B
      - Both nodes may still be running, but messages between them are delayed, dropped, or indefinitely unavailable.
    - Partition tolerance means the system continues operating despite this communication failure.
    - In practical distributed systems, network partitions must generally be considered possible.
    - Therefore, the important design decision during a partition is usually :
      - Consistency or availability?
- The core CAP trade-off
  - A client writes, x = 10
  - The write reaches Replica A but cannot Replica B
  - Now a client reads from Replica B
  - The system has two broad choices
    - Option 1 : Preserve consistency
      - Replica B refuses the read or reports that it cannot safely anser
        - Read -> error
      - Result
        - Consistency preserved
        - Availability sacrificed
    - Option 2 : Preserve availability
      - Replica B returns its local value, possibly an older value
        - Read -> old value
      - Result
        - Availability preserved
        - Consistency sacrificed
  - This is the central CAP trade-off during a partition
- The misleading "pick any two" explanation
  - The common explanation is : 
    - You can choose only two of :
      - Consistency
      - Availability
      - Partition tolerance
  - This is useful as an introduction but incomplete
  - A better explanation is : 
    - When a partition occurs, a distributed system cannot simultaneously guarantee strong consistency and availability for the same data
  - Partition tolerance is not usually an optional feature in a networked distributed system.
  - Therefor , real design discussions usually compare : 
    - CP : Consistency + Partition tolerance
    - AP : Availability + Partition tolerance
- CP systems
  - prefers consistency during a partition
    - Partition occurs
    - Cannot safely coordinate replicas
    - Reject or delay some requests
  - CP behavior is appropriate when stale or conflicting data is dangerous.
    - Bank balances
    - Payment State
- AP systems
  - prefers availability during a partition
    - Partition occurs
    - Reachable replicas continue responding
    - Replicas may temporarily disagree
  - usually require
    - Conflict resolution
    - Versioning
    - Repair
    - Eventual convergence
  - AP behavior may be appropriate for : 
    - Social-media reactions
    - Product recommendations
- Strong consistency
  - provides a simple model for clients
    - Write value X
    - Successful response
    - Later reads see X
  - simplifies logic as clients do not need to reason about stale read
  - Costs
    - Higher latency
    - Lower availability
    - Coordination overhead
    - Leader dependency
    - Cross-region communication
  - often worth the cost for correctness-critical state
- Eventual consistency
  - allows temporary divergence :
    - Replica A -> value 20
    - Replica B -> value 16
  - after replication catches up
  - the basic guarantee is : 
    - if no new updates occur and communication eventually recovers, replicas converge.
  - Eventually consistency improves availability, reduce write latency, but observe
    - Older values
    - Different values on different read
    - Result that do not uet include recent write
- Read-you-writes consistency
  - ensures after my write succeeds, my later reads observe that write
  - possible implementation : 
    - Read from the primary after writing
    - Route the use temporarily to the write replica
    - Use a session or request version
    - Wait until replicas catch up
- Monotonic reads
  - ensures that a client does not move backward in observed versions
  - Bad behavior : 
    - Read 1 -> version 5
    - Read 2 -> version 4
  - can be supported by :
    - Sticky routing
    - Replica version tracking
    - Session consistency
    - Read-from-primary rules
- Linearizability
  - each operation appears to take effect atomically at some point between its invocation and completion, while respecting real-time ordering
  - Consider
    - Client A writes x = 10
    - Write completes
    - Client B reads x
  - A linearizability system must return
    - x = 10
  - cannot return older value after the completed write
  - it is a strong consistency model, useful for :
    - Leader election
    - locks
    - unique resource allocation
- Serializability vs linearizability
  - related but different
  - Serializability
    - concerns transaction ordering : 
      - concurrent transaction produce the same result as serial execution
  - Linearizability
    - concerns real-time ordering of individual operations:
      - Completed operation must appear before a later operation that starts afterward
  - A database can provide serializable transaction without every external read behaving as linearizable
- CAP and databases
  - when evaluating a distributed database, ask
    - What happens during a partition?
  - Single leader
    - Leader accepts writes
    - followers replicate
    - during leader isolation
      - no new writes OR
      - leader election required
    - favors consistency
  - Multi-Leader
    - Region A accepts writes
    - Region B accepts writes
    - communication failure
      - both accepts conflicting writes
    - favors availability but requires conflict resolution
  - Leaderless or quorum-based
    - Write for to multiple replicas
    - reads consult multiple replicas
    - consistency depends on :
      - Replication factor N 
      - Write quorum W
      - Read quorum R
- CAP vs normal operation
  - during normal operation
    - Low latency reads
    - Strong reads
    - Asynchronous replication
  - partition occurs : 
    - Reject requests
    - Return stale data
  - always ask : 
    - What is the behavior during failure ?
- Conflict resolution
  - AP systems need a strategy for concurrent updates
    - Last write wins
      - value with the later timestamp wins
      - Problem:
          - Clock difference can produce surprising results
    - First write wins
    - Version vectors
    - Application specific merge
    - CRDT based merge
    - Manual resolution
- Common CAP mistakes
  - Choose any two at all times
    - the key trade=off appears during network partition
    - a system cannot guarantee both strong consistency and availability during that partition
  - Partition tolerance means data partitioning
    - In CAP, partition tolerance mean tolerating a communication partition between nodes, not database sharding
  - AP means no consistency
    - AP system may provide eventual, casual, session or other weaker consistency guarantee
  - CP means the whole system is always down
    - A CP system may continue serving operations that can be safely cordinated while rejecting or delaying operations affected by the partition
  - CAP tells you which database to choose
    - CAP helps reason about failure behavior, but database selection also depends on the query patter, transactions, latency, cost, and operational requirements
- Decision framework
  - for each data type ask : 
    - What happens if a read is stale ?
    - What happens if two writes conflict?
    - Can the system reject writes temporarily?
    - Must the operation be globally ordered?
    - What latency is acceptable?
    - What happens during a regional partition?

| Data                  | Preferred behavior                     |
|-----------------------|----------------------------------------|
| Payment ledger        | Strong consistency                     |
| Inventory reservation | Strong consistency                     |
| Product catalog       | Eventual consistency may be acceptable | 
| Search index          | Eventual consistency                   | 
| Analytics             | Eventual consistency                   |
| Distributed lock      | Linearizable behavior                  |
| User preferences      | Session / read-you-writes consistency  |


```text
CAP describes the tradeoff during a network partition:
a distributed system cannot guarantee bth strong consistency and availability for the same operation
CP sysetms reject or delay unsafe operations, while AP systems continue responding and 
reconcile divergent data later.
The correct choice depends on business semantics , not on a blanket label for the entire application
```