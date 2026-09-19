#

##

## My summary

- Runnable represents a task that:
  - takes no input
  - returns no result
  - cannot directly throw checked exceptions
- `Callable<T>` represents a task that :
  - returns a result
  - can throw checked exception
  - unlike runnable, a callable is usually submitted to an executor.
- A thread pool : 
  - Reuses worker threads
  - Limits concurrency
  - Reduces thread creation overhead
  - Manages task execution
  - Tasks -> Queue -> Worker threads
- `shutdown()` stops accepting new tasks but allows  submitted tasks to finish
- `Future<T>` represents the result of an asynchronous computation.
- `get()` blocks until task completes
- `execute()` : Accepts Runnable and returns nothing
- `submit()`: Accepts Runnable or Callable and returns a Future
  - A result
  - Completion tracking
  - Exception inspection
- `ExecutorService executor = Executors.newFixedThreadPool(3)`
  - at most three tasks execute simultaneously.
  - Additional tasks wait in the executor's queue
  - Use this when you want predictable concurrency
- `ExecutorService executor = Executors.newSingleThreadExecutor()`
  - when Tasks must be serialized
  - shared state needs one worker
  - want simple background processing
- `shutdown()` allows existing tasks to complete
- `shutdownNow()` attempts to interrupt running tasks and returns waiting tasks

```text
`ExecutorService` manages a pool of reusable worker threads
`Runnable` represents a task without a result, while `Callable` can return a result through `Future`
Executors should be shutdown explicitly when they ar eno longer needed.
```