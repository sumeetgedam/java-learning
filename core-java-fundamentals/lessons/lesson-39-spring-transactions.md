# Lesson 39: Spring Transactions

## Questions

1. What is a database transaction?
2. What do the ACID properties mean?
3. What does @Transactional do?
4. What infrastructure is required for @Transactional to work?
5. What is the default rollback behavior?
6. How can checked exceptions trigger rollback?
7. What does readOnly mean?
8. What is transaction propagation?
9. What does REQUIRED mean?
10. What does REQUIRES_NEW mean?
11. What is transaction isolation?
12. What is a dirty read?
13. Why can self-invocation bypass transaction behavior?
14. Why do transactions not automatically propagate to new threads?
15. Why can external calls inside transactions be risky

## My summary

- Spring provides a consistent transaction abstraction across JDBC, JPA, Hibernate, and other data-access technologies
- Declarative transactions are implemented using AOP proxies and a transaction manager.
- A transaction groups related operations into one unit of work
  - Transfer $100
    - Remove $100 from Account A
    - Add $100 to Account B
  - Both operations should succeed together or neither should take affect.
- ACID properties

| Property    | Meaning                                              |
|-------------|------------------------------------------------------|
| Atomicity   | All operations succeed or all are rolled back        |
| Consistency | Data remains valid according to defined rules        |
| Isolation   | Concurrent transactions do not interfere incorrectly |
| Durability  | Committed data survives successfully                 |

- Spring separates transaction logic from application code using abstractions such as:
  - PlatformTransactionManager
  - TransactionDefinition
  - TransactionStatus
- The concrete transaction manager depends on the technology:
  - JDBC -> DataSourceTransactionManager
  - JPA -> JpaTransactionManager
  - Hibernate -> HibernateTransactionManager
  - JTA -> JtaTransactionManager
  - The correct transaction manager must be configured for the data-access technology being used.
- `@Transactional`
  - This declares that a method or class should run with transactional behavior
  - The annotation is metadata
  - it becomes effective only when Spring's transaction infrastructure is enabled and the bean is managed by Spring.
- Rollback behavior
  - By default, Spring rolls back a declarative transaction for : 
    - `RuntimeException`
    - `Error`
  - Checked exception do not cause rollback by default
  - Use `rollbackFor`
    - `@Transactional(rollbackFor = IOException.class)`
  - Use `noRollbackFor` when a particular exception should not trigger rollback
    - `@Transactional(noRollbackFor = NotificationException.class)`
    - This can be useful when a secondary operation fails but the main transaction should still commit.
- All public methods use the class-level defaults
  - Method-level settings take precedence over class-level settings.
- Use `readOnly = true` for read operations
  - This communicates intent and may allow the underlying data-access technology to optimize the operation.
  - It does not necessarily prevent every possible write
  - Treat is as a transaction hint and semantic declaration rather than a universal write-protection mechnism
- Propagation
  - It defines what happens when a transactional method calls another transactional method.
  - Most common setting is : `Propagation.REQUIRED`
    - join the existing transaction if one exists, otherwise create a new transaction.
    - `@Transactional(propagation = Propagation.REQUIRED)`
    - outer transaction --> inner method joins outer transaction
  - `REQUIRES_NEW`
    - Suspend the existing transaction and create a new one.
    - `@Transactional(propagation = Propagation.REQUIRES_NEW)`
    - outer transaction suspended -> new audit transaction -> audit commits -> outer transaction resumes
  - `SUPPORTS`
    - Use the existing transaction if one exists, otherwise execute without a transaction.
    - `@Transactional(propagation = Propagation.SUPPORTS)`
  - `MANDATORY`
    - Require an existing transaction
    - `@Transactional(Propagation = Propagation.MANDATORY)`
    - if no transaction exists, Spring throws an exception.
  - `NEVER`
    - Requires that no transaction exists
    - `@Transactional(propagation = Propagation.NEVER)`
    - fail if a transaction is present
  - `NOT_SUPPORTED`
    - Suspend any existing transaction and run without one.
    - suspend a transaction if present.
- Isolation
  - Isolation controls what one transaction can observe while other transactions are running
    - Common isolation levels :
      - `Isolation.DEFAULT`
      - `Isolation.READ_UNCOMMITTED`
      - `Isolation.READ_COMMITTED`
      - `Isolation.REPEATABLE_READ`
      - `Isolation.SERIALIZABLE`
  - Common anomalies
    - Dirty read
      - A transaction reads data written by another transaction that has not commited.
    - Non-repeatable read
      - The same query returns different values during one transaction because another transaction committed an update.
    - Phantom read
      - A repeated query returns a different f rows because another transaction inserted or removed matching rows.
  - Higher isolation generally provides stronger consistency but may reduce concurrency.
- Timeout
  - Set a transaction timeout
  - `@Transactional(timeout = 5)`
  - units are seconds
  - do not create as a replacement for :
    - Query optimization
    - Connection-pool configuration
    - External-call timeouts
    - Proper cancellation
- Spring's transaction abstraction exposes propagation, isolation, timeout and read-only settings, while the data and transaction manager determine the concrete behavior.
- Place transaction boundaries around business operations
  - the service layer is a good place for transaction boundaries because it coordinates multiple data-access operations
  - Avoid placing transactions only around individual low-level repository operations when a business operation requires several operations to succeed together.
- Spring's default transaction mode is proxy-based
  - works when the call comes from another Spring bean through the proxy
  - Better options :
    - Move the transaction method to another bean
    - Call through a separate collaborator
    - Redesign the service boundary
    - Use AspectJ mode only when its complexity is justified.
- A typical Spring transaction is bound to the current execution thread
  - Starting a new thread inside a transactional method does not automatically propagate the transaction to that thread.
- A local database transaction does not automatically include :
  - Email delivery
  - HTTP requests
  - Message publishing
  - Filesystem writes
  - Remote service operations
  - possible solutions
    - Outbox pattern
    - Message-driven processing
    - Idempotent external operations
    - Compensating actions
    - Distributed transaction protocols when genuinely required.
  - Do not keep a database transaction open while performing slow remote calls unless the design explicitly requires it.


```text
Spring transactions group related data operations into one unit of work.
@Transactional is applied through Spring's transaction proxy and requires a transaction manager.
Understand rollback rules, propagation, isolation, and proxy limitations before relying on the annotation.
```