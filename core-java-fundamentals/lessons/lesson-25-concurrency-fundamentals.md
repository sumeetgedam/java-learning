# Lesson 25: Concurrency Fundamentals

## Questions

1. What is a process?
2. What is a thread?
3. What memory do threads in the same process share?
4. What is the difference between start() and run()?
5. What does sleep() do?
6. What does join() do?
7. What is a race condition?
8. Why is value++ not necessarily atomic?
9. What is a critical section?
10. What does synchronized protect?
11. What is thread interference?
12. What is thread state?

## My summary

- Process 
  - independent execution environment
  - own memory space
- Threads
  - runs inside a process 
  - share process memory.
  - Cheaper than creating another process
  - Can accidentally interfere with other threads

Process
|-- Thread 1
|-- Thread 2
|-- Thread 3

- `start()` creates a new execution path and eventually invokes run()
  - do not call `run()` dorectly if you want a new thread.
  - `run()` is normal method call.
- `sleep()` temporarily pauses current thread execution
- `join()` makes one thread wait for another to finish
- Thread Lifecycle : 
  - NEW -> start()
  - RUNNABLE -> scheduled/executing
  - TERMINATED
- Synchronization prevents multiple threads from entering the synchronized method on the same object at the same time.


```text
A thread is an execution path inside a process
Threads share memory, which makes communication easy but creates race conditions
when shared mutable state is accessed without coordination.
`start()` begins concurrent execution, while `run()` is only a normal method call.
```