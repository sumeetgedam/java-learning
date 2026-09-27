# Lesson 58: Replication, Quorums, and Leader Election

## Questions

1. Why is data replicated?
2. What is replication factor?
3. What is synchronous replication?
4. What is asynchronous replication?
5. What is replica lag?
6. What is a quorum?
7. How is majority quorum calculated?
8. Why are odd-sized consensus clusters common?
9. What are read and write quorums?
10. What does R + W > N mean ?
11. Why does quorum overlap not automatically guarantee strong consistency?
12. What is leader-based replication?
13. What happens during leader election?
14. What are the follower, candidate and leader roles in Raft?
15. What are terms or epochs?
16. What is split-brain?
17. What is a fencing token?
18. What is the difference between replicated and committed data?
19. What are kafka leaders, replicas, and ISR?
20. What do ack=all and min.insync.replicas control?
21. Why can a process be alive but unable to server consensus-dependent writes?
22. Why can election timeouts be too short or too long?

## My summary

- Why replication ?
  - Replication stores copies of data on multiple nodes
    - Primary
      - Replica A
      - Replica B
      - Replica C
  - Replication provides
    - Higher availability
    - Failover
    - Read scaling
    - Data durability
    - Fault tolerance
  - if one node fails :
    - Primary fails
    - Another replica may become primary
  - Replication does not automatically guarantee consistency, the system must define
    - When replicas receive writes
    - Which reads are allowed
    - How conflicts are handled
    - When a replica is eligible for promotion
- Replication factor
  - The replication factor is the number of copies maintained for each piece of data
  - Replication factor = 3
    - Copy 1 -> Node A
    - Copy 2 -> Node B
    - Copy 3 -> Node C
  - for kafka, replication factor and partition count are separate concepts
    - Partitions
      - how data is divided
    - Replication factor
      - how many copies of each partition exists
- Synchronous replication
  - write is acknowledged only after required replicas confirm it
    - Client
    - Primary
      - writes locally
      - sends to Replica A
      - sends to Replica B
      - acknowledgments
      - client response
  - disadvantages
    - higher write latency
    - write failure when replicas are unavailable
    - cross-region coordination cost
  - useful for
    - payment records
    - inventory reservations
    - financial ledgers
- Asynchronous
  - the primary can acknowledge before all replicas receive the write.
    - client
    - primary writes
    - primary responds
    - replicas catch up later
  - advantages
    - lower write latency
    - higher availability
    - better geographic distribution
  - disadvantages
    - Replica lag
    - Possible data loss if primary fails
    - stale reads
    - more complex failover
  - acceptable for
    - Search index
    - Analytics
    - recommendations
    - read-heavy catalog data
- Primary-replica architecture
  - uses one primary for writes and multiple replicas for reads
  - Write flow
    - Client -> Primary -> replicas
  - Read flow
    - Client -> Replica A
  - Benefits :
    - Centralized write ordering
    - Read scaling
    - Relatively simple conflict model
  - Risks :
    - Primary can become a bottleneck
    - Primary failure requires failover
    - Replica reads may be stale
    - incorrect routing can send writes to replicas
- Replica lag
  - delay between a write being accepted by the primary and being applied by a replica
  - can be caused by :
    - Network delay
    - slow disk
    - high write volume
    - long-running queries
    - consumer backlog
    - replication bandwidth limits
- Read-after-write problem
  - as replicas sometimes may give stale data
  - solutions
    - Read from primary after write
      - writes       ----> Primary
      - Recent reads ----> Primary
    - Stick routing
      - route a user to the primary or a sufficiently current replica temporarily
    - Version-aware reads
      - write returns a version
      - the read only uses a replica that has reached version
    - Synchronous replication
      - wait for required replicas before confirming write
- Quorum
  - It is the minimum number of nodes required to agree before an operation succeeds
  - for a cluster of N nodes, a simple majority quorum is : 
    - quorum = floor(N / 2) + 1
- Read and write quorums
  - For a replicated data store
    - N = total replicas
    - W = replicas required for a write
    - R = replicas consulted for a read
  - commonly discussed overlap
    - R + W > N
    - this ensures that the read and write sets overlap by at least one replica
- Leader-based replication
  - leader based system has one node responsible for ordering writes
    - Clients
      - Leader
        - Follower A
        - Follower B
        - Follower C
  - Write flow  :
    - Client sends command to leader
    - Leader appends command to its log
    - Leader replicates command
    - Required replicas acknowledge
    - entry becomes committed
    - leader responds
  - model simplifies : 
    - write ordering 
    - conflict avoidance
    - Log replication 
    - client routing
- Leader election
  - chooses a new leader when the current leader
    - crashes
    - becomes unreachable
    - stops responding
    - loses quorum
    - becomes too slow
  - basic flow
    - leader sends heartbeats
    - follower receive heartbeats
    - heartbeat stops
    - follower timeout expires
    - follower becomes candidate
    - requests votes
    - majority votes received
    - new leader elected
  - leader election requires
    - failure detection
    - candidate terms or epochs
    - voting rules
    - majority agreement
    - protection against stale leaders
- Raft roles
  - uses three primary roles : 
    - follower
      - Receives replication messages
      - responds to leader heartbeats
      - votes in elections
      - does not normally accept client writes
    - candidate 
      - Starts an election
      - increments its term
      - requests votes
      - becomes leader if it receives a majority
    - leader
      - handles client command
      - replicates log entries
      - sends heartbeat
      - advances the commit index
  - Raft uses randomized election timeouts and majority voting to reduce split votes and ensure that at most one leader is elected for a term
- Terms and epochs
  - A term is a logical period of leadership
    - Term 1 -> Leader A
    - Term 2 -> Leader B
    - Term 3 -> Leader C
  - helps identify stale messages
    - old leader frm term 4 sends a message
    - current cluster is in term 5
  - prevents old leader from continuing to mak decisions 
- Split-brain
  - occurs when multiple groups or nodes believe they are the leader
    - Partition :
      - group A believes Leader A is valid
      - group B believes Leader B is valid
  - Quorum based election prevents this
    - majority -> may elect leader
    - minority -> cannot elect leader
- Fencing tokens
  - A stale leader may continue after losing leadership because :
    - it has not noticed the partition
    - its network is delayed
    - its process is paused
    - its clock is wrong
  - A fencing token prevents stale leaders from modifying shared state
    - Leader A receives token 10
    - Leader B later receives token 11
  - useful for
    - Distributed locks
    - storage writers
    - job ownership
    - scheduled task execution
  - Log replication
    - a leader-based consensus system often replicates a log
      - Leader log
        - create-order
        - reserve-inventory
        - charge-payment
      - Follower log
        - create-order
        - reserve-inventory
      - follower catches up with 
        - charge-payment
- Committed vs replicated
  - Replicated
    - a record exists on more than one node
  - Committed
    - the protocol has determined that the record is safe and part of the durable agreed history
  - the entry may exist on multiple nodes but still require protocol-specific treatment
  - Do not assume
    - Present on a follower = committed
- Kafka replication model
  - It replicates each partition across brokers
    - orders-0
      - Broker 1 : leader
      - Broker 2 : follower
      - Broker 3 : follower
  - for a partition, kafka tracks
    - leader
    - replicas
    - in-sync replicas(ISR)
- Kafka acks and min.insync.replicas
  - for stronger durability, a common kafka configuration is : 
    - replication.factor = 3
    - min.insync.replicas = 2
    - acks = all
      - means acknowledgement from all current in-sync replicas
      - not necessarily every replica ever assigned to the partition
  - conceptually :
    - at least two in-sync replicas
    - producer waits for all currently in-sync replicas
    - write succeeds
  - if in-sync replicas count falls below min.insync.replicas, the write can fail rather than accepting a less durable write
- Kafka ISR
  - It is the set of replicas considered sufficiently caught up to paritipate in the partition's replication guarantees
    - Assigned replicas
      - Broker 1, Broker 2, Broker 3
    - Current ISR : 
      - Broker 1, Broker 2
  - if broker 3 falls behinds it may leave the ISR
  - When ISR shrinks
    - durability decreases
    - write availability may decrease
    - failover choices may change
- Leader failure scenario
  - Initial state : 
    - Broker 1 -> leader
    - Broker 2 -> ISR follower
    - Broker 3 -> ISR follower
  - Broker 1 fails
  - possible flows
    - Controller detects failure
    - Eligible replicas is selected
    - Broker 2 becomes leader
    - Producers refresh metadata
    - Clients continue with Broker 2
  - If only one replica has the latest committed data, systm may ned to choose between
    - Availability
      - Promote a less-current replicas
    - Durability
      - refuse leadership until safe data is available
- Odd-sized clusters
  - for a majority based consensus
    - 3 nodes -> quorum 2 -> tolerate 1 failure
    - 5 nodes -> quorum 3 -> tolerate 2 failure
  - adding a fourth node to a three node cluster does not improve failure tolerance
    - 3 nodes : 
      - quorum = 2
      - tolerate 1 failure
    - 4 nodes : 
      - quorum = 3
      - tolerate 1 failure
- Leader election and timeout
  - Leader election depends on detecting that the leader is unavailable 
  - too-short timeout
    - Normal network delay
    - false failure detection
    - unnecessary elections
  - too-long timeout
    - Actual leader failure
    - long period before recovery
  - election timeout should account for : 
    - Network round-trip time
    - disk latency
    - Scheduling pauses
    - GC pauses
    - Load spikes
    - Cross zone latency
- Summary
  - Replication
    - Keeps multiple copies of data for availability, durability and failover
  - Quorum
    - Requires enough nodes to agree so that competing groups cannot both make authoritative decisions
  - Leader election
    - Chooses one node to coordinate ordered writes after the current leader fails
  - Split brain prevention
    - Uses terms, epochs, quorum voting, leases, or fencing tokens to prevent stale leaders from continuing to write
  - Kafka
    - Replication partitions across brokers, tracks in-sync replications, and uses producer acknowledgement and min.insync.replicas settings to control durability


```text
Replication creates multiple data copies, while quorum determine how many nodes must participate in reads,
writes, or leadership decisions. Leader-based systems use elections, terms, and majority voting to prevent
split-brain. Kafka applies similar replication concepts through partition leaders, followers, ISR, producer 
acknowledgements, and minimum in-sync replica settings 
```