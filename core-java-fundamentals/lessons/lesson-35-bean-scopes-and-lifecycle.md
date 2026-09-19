# Lesson 35: Bean Scopes and Lifecycle

## Questions

1. What is the default Spring bean scope?
2. What does singleton mean in Spring?
3. What does prototype scope mean?
4. Which scopes require a web-aware ApplicationContext?
5. What does @PostContruct do?
6. What does @PreDestroy do?
7. Why must an ApplicationContext be closed?
8. Why can singleton beans be unsafe when they contain mutable state?
9. What happens when a singleton depends directly on a prototype bean?
10. How can ObjectProvider obtain new prototype instances?
11. Why are prototype destruction callbacks special?
12. When should initialization and cleanup callbacks be used?

## My summary

- A Spring Bean definition describes hw the container creates and manages object instances,
- The default scope is singleton, meaning one instance per Spring container
- Other scopes determine when additional instances are created.
- Singleton scope
  - this one is default
  - Spring singleton means one instance per container, not necessarily one instance for entire JVM.
  - A singleton bean may be accessed by many application threads
  - Avoid mutable request-specific state
  - Prefer stateless services.
- Prototype scope
  - A prototype bean creates a new instance each time the container is asked for it
  - Spring initializes prototype beans, but it generally does not manage their complete destruction lifecycle 
  - The client is responsible for releasing resources held by prototype objects.

| Scope         | Instance behavior               |
|---------------|---------------------------------|
| `singleton`   | One instance per container      |
| `prototype`   | New instance each lookup        |
| `request`     | One instance per HTTP request   |
| `session`     | One instance per HTTP session   |
| `application` | One instance per ServletContext |
| `Websocket`   | One instance per Websocket      |

- `@PostConstruct`
  - Use @PostConstruct for initializatin after dependency injection is complete.
  - Typical uses :
    - Validate required configuration
    - Prepare in-memory data
    - Initialize a client
    - Log startup information
  - Do not use it for long-running work that delays application startup unnecessarily.

- `@PreDestroy`
  - Use @PreDestroy for cleanup before the bean is destroyed 
  - Appropriate clean includes :
    - Closing a custom resource
    - Stopping an internal worker
    - Releasing a client
    - Flushing buffered data
  - For files, sockets and database resources, prefer APIs that clearly own and manage the resource, such as try-with-resources where appropriate
- For lifecucle destruction callbacks to run, close the context :
  - `close()` operation allows the context to release resources and invoke applicable destruction callbacks.
- Lifecycle sequence
  - Instantiate Bean
  - Inject dependencies
  - Apply bean post-processors
  - Run @PostContruct
  - Bean is ready
  - Application uses bean
  - Context closes
  - Run @PreDestroy
  - Bean is destroyed
- Prototype lifecycle warning
  - The initialization callback runs when Spring creates the prototype
  - However, Spring does not normally call the destruction callback when the prototype is no longer needed.
  - The client must manage cleanup.
- Singleton depending on prototype
  - Although TaskContext is prototype-scoped, it is injected only once when the singleton TaskService is created.
  - It does not automatically receive a new TaskContext for every method call.
  - Spring documents this as a limitation of injecting a prototype dependency into a singleton
  - For repeated prototype lookup, options include : 
    - ObjectProvider<T>
    - Provider<T>
    - Method injection
    - Redesigning the object boundary
- Lifecycle callbacks also works with @Bean methods
  - `@Bwan(initMethod = "start", destroyMethod = "stop")`
  - Spring supports lifecycle for beans decalred with !Bean, including configured initialization and destruction methods.


```text
Bean scope controls how Spring creates and reuses instances.
Singleton beans are shared within a container, prototype beans are created for each lookup, and lifecycle callbacks allow
initialization and cleanup around the bean's managed lifetime.
```