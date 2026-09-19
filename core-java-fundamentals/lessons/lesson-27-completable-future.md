# Lesson 27: CompletableFuture

## Questions

1. What is CompletableFuture?
2. What is the difference between runAsync and supplyAsync?
3. What does thenApply do?
4. What is the difference between thenApply and thenAccept?
5. When would you use thenCompose?
6. When would you use thenCombine?
7. What does exceptionally do?
8. What is the difference between handle and whenComplete?
9. What does allOf do?
10. Why might you provide a custom Executor?
11. What is the difference between thenApply and thenApplyAsync?
12. What does join() do?

## My summary

- `CompletableFuture` implements both `Future` and `CompletionStage`
  - allowing us to represent an asynchronous result and attach dependent actions to it.
- `runAsync` is used where there is no result
- Without an explicit executor, async methods generally use the common ForkJoinPool.
- Use `thenApply` to transform a result
  - continuation may ru in the thread that completes the previous stage
- Use `thenAccept` when you want to consume a result without returning another result.
- Use `thenRun` when you want to execute an action after completion and do not need the previous result.
- Use `thenCompose` when the next operation itself returns a `CompletableFuture`
  - Future<A> -> asynchronous function A -> Future<B>
- Use `thenCombine` when two independent asynchronous operations must both complete.
- `exceptionally` provides a fallback value when the pipeline fails
- Use `handle` when you want access to either the result or the exception.
  - always produces a result
- Use `whenComplete` for observation or logging
  - unlike `handle`, `whenComplete` normally does not transform the result
- `thenApplyAsync`
  - continuation is scheduled asynchronously, normally using the default async executor
- Use `allOf` to wait for multiple futures
  - returns CompletableFuture<Void>, so retrieve individual results separately


```text
`CompletableFuture` represents an asynchronous result and allows opertions to be chaned, combined, and recovered from failures.
`thenApply` transforms a result, `thenCompose` chains asynchronous operations, and `thenCombine` joins independent operations.
```