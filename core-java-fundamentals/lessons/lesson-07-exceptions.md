# Lesson 7: Exceptions

## Questions

1. What is an exception?
2. What is the difference between checked and unchecked exceptins?
3. What is the purpose of `try`?
4. What is the purpose of `catch`?
5. When does `finally` execute?
6. What is the difference between `throw` and `throws`?
7. Why should exceptions not be silently ignored?
8. When would you create a custom exception?
9. What is try-with-resources?
10. What does exception propogation mean?


## My summary

- An exception is an event that interrupts normal program execution.
- finally block executes whether an exception occurs or not
  - Closing resources
  - Releasing locks
  - Cleanup operations
- Unchecked exceptions extend RuntimeException.
  - Usually represents programming errors or invalid runtime input
  - NullPointerException
  - IllegalArgumentException
  - IndexOutOfBoundsException
  - ArithmeticException
- Checked exceptions are verified by compiler
  - IOException
  - Handle the exception with try-catch
  - Declare it with throws
- throw creates and raises an exception
- throws declares multiple possible exceptions that a method may pass an exception to its caller.
- prefer try-with-resources over manually closing resource in finally.

```text
Exceptions represent abnormal conditions.
A method can handle an exception, propagate it to its caller, 
or declare that it may throw one.
Specific exceptions should be handled meaningfully rather than hidden.
```