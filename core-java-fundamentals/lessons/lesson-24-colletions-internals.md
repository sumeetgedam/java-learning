# Lesson 24: Collections Internals

## Questions

1. What does Big-O notation describe?
2. Why is Arraylist indexed access usually O(1)?
3. Why can inserting into the middle of an ArrayList be O(n)?
4. Why is LinkedList random access usually O(n) ?
5. How does HashMap use hashCode() and equals()?
6. What is a hash collision?
7. When would you use LinkedHashMap?
8. When would you use TreeMap?
9. What is the difference between Queue and Deque?
10. How does PriorityQueue determine removal order?
11. What is fail-fast iteration?
12. Why should fail-fast behavior not be used for program correctness?

## My summary

- Big-O basics
  - Describes hwo an operation grows as the collection size increases.

| Complexity | Meaning                          | 
|------------|----------------------------------| 
| O(1)       | Constant time                    | 
| O(log n)   | Grows logarithmically            | 
| O(n)       | Linear scan                      | 
| O(n log n) | Common sorting complexity        | 
| O(n2)      | Usually expensive for large data | 

- ArrayList :
  - need index access
  - mostly append
  - frequently iterate
  - do not frequently insert/remove from the middle
- LinkedList :
  - its a double linked list
  - use when its linked-node behavior fits the case.
- HashMap
  - Stores key-value pairs using hashing.
  - Key -> hashCode() -> bucker -> key comparison using equals()
  - capacity: number of available bucket
  - Load factor : how full it can become before resizing.
- HashSet
  - backed by hash table and is intended for unique values.
  - does not guarantee interation order
- LinkedHashMap and LinkedHashSet :
  - preserve insertion order
- TreeMap and TreeSet : 
  - maintain sorted order
  - based on balanced tree structure
    - TreeMap : red-black tree
    - TreeSet : sorted-set counterpart
  - use when :
    - Sorted keys
    - Range queries
    - First/last values
    - Ceiling/floor operations
- Queue, Deque and PriorityQueue
  - Queue : 
    - FIFO
  - Deque
    - supports insertion and removal at both ends
  - PriorityQueue
    - removes elements according to priority, not insertion order.
    - docs describes it as heap implementation


```text
Choose collections based on required behavior: 
`ArrayList` for indexed access,
hash collections for expected constant-time lookup,
tree collections for sorted operations,
deques for both-end operations, and 
priority queues for priority-based retrieval,
```