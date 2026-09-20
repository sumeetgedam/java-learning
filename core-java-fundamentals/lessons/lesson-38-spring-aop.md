# Lesson 38: Spring AOP

## Questions

1. What is a cross-cutting concern?
2. What is an aspect?
3. What is a join point?
4. What is a pointcut?
5. What is advice?
6. What is the difference between @Before and @Around?
7. Why must around advice usually call proceed()?
8. What does @AfterReturning observe?
9. What does @AfterThrowing observe?
10. How does Spring AOP apply advice?
11. What is a Spring AOP proxy?
12. Why does self-invocation bypass advice?
13. When should AOP be avoided?
14. What is the difference between JDK and CGLIB proxies?

## My summary

- Spring AOP modularize behavior that cuts across multiple classes, such as logging, security, transactions, metrics, and auditing.
- Spring AOP is proxy-based and primarily intercepts method executions on Spring-managed beans.
- If every service method contains timing, logging, authorization, and error handling, business code becomes difficult to read.
  - These concern are called cross-cutting concerns because they affect multiple classes
- Core AOP terminology

| Term          | Meaning                                         |
|---------------|-------------------------------------------------|
| Aspect        | A module containing cross-cutting behavior      |
| Join point    | A point during execution where advice can run   |
| Pointcut      | A rule that selects join points                 |
| Advice        | Code executed at a selected join point          |
| Target object | The original object being advised               |
| Proxy         | Object that intercepts calls and applies advice |

- Advice types
  - Spring supports several advice types : 
    - `@Before`
      - Runs before the target method
      - `@Before(execution(* com.learning.spring.service.*.*(..))")`
      - It cannot directly change the method's return value
    - `@AfterReturing`
      - Runs after successful completion
      - `@AfterReturing(point="...", returning="result")`
    - `@AfterThrowing`
      - Runs when the target method throws an exception
        - `@AfterThrowing(pointcut="...", throwing="exception")`
    - `@After`
      - Runs after completion regardless of success or failure
    - `@Around`
      - Surrounds the method invocation and controls whether the target method proceeds.
    - Around advice is the most powerful advice type, but use the least powerful advice that solves the problem
- `@EnableAspectJAutoProxy` enables automatic creation of proxies for beans matched by `@Aspect` pointcuts.
- Spring interprets AspectJ-style annotations, but the runtime remains Spring AOP rather than requiring AspectJ compile-time weaving
- Pointcut expression examples
  - All public methods in a package
    - execution(public * com.learning.spring.service.*.*(..))
  - Any method named findUser
    - execution(* com.learning.spring.aop.UserService.findUser(..))
  - Methods beginning with find
    - execution(* com.learning.spring.aop.UserService.find*(..))
  - Any method in a package and subpackages
    - execution(* com.learning.spring..*(..))
  - Methods annotated with a custom annotation
    - @annotation(com.learning.spring.aop.Audited)
- Self-invocation through `this` bypasses proxy-based advice.
  - Better design includes :
    - Move the advised method to another bean
    - Call the method through separate injected collaborator
    - Use an application event or explicit decorator
    - Avoid exposing AOP-sensitive internal calls.
- AOP best pratices
  - Good candidates :
    - Logging
    - Metrics
    - Auditing
    - Transactions
    - Security checks
    - Retry policies
  - Avoid using AOP to hide business rules.
  - Prefer the last powerful advise :
    - Need only before behavior -> @Before
    - Need successful return value -> @AfterReturning
    - Need failure observation -> @AfterThrowing
    - Need guaranteed cleanup -> @After
    - Need control around invocation -> @After

```text
Spring AOP applies crodd-cutting behavior through proxies.
A pointcut selects method executions, advice defines what happens at those points, 
and aspects combine pointcuts with advice
Because Spring AOP is proxy-based, direct self-invocation does not pass through the proxy.
```