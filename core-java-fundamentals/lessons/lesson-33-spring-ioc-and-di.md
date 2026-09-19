# Lesson 33: Spring IoC and Dependency Injection

## Questions

1. What is Inversion of Control?
2. What is Dependency Injection
3. What is a Spring Bean?
4. What is ApplicationContext?
5. What does @Configuration mean?
6. What does @Bean mean?
7. Why is constructor injection useful?
8. What are the drawbacks of field injection?
9. How does Spring build a dependency graph?
10. What happens when multiple beans match one dependency?
11. How can a bean be retrieved from ApplicationContext?
12. Why should services depend on abstractions?

## My summary

- Spring's IoC container creates, configures, and assembles application objects called `beans`.
- Dependency Injection is the mechanism through which the container supplies an object's dependencies
- Inversion of Control
  - Application code :
    - describes dependencies
  - Spring container :
    - creates objects
    - connects dependencies
    - manages lifecycle
  - Instead of a class creating its dependency, the dependency is provided from outside.
- `Spring Bean` 
  - A bean is an object instantiated, assembled, and managed by the Spring IoC container.
  - examples 
    - UserService
    - OrderRepository
    - EmailSender
    - PaymentProcessor
    - ApplicationConfiguration
  - An ordinary Java object becomes Spring bean when it is registered with the container
- `ApplicationContext`
  - It is a commonly used Spring IoC container interface.
  - It manages bean creation and adds features such as event publication, resource handling, and integration with other Spring facilities.
  - `ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)`
  - `NotificationService service = context.getBean(NotificationService.class)`
- Java based configuration
  - Use `@Configuration` to define a configuration class and `@Bean to declare objects managed by Spring.
  - A `@Bean` method creates and configures an objet for the IoC container
- Dependency graph
  - ApplicationContext
    - NotificationSender
      - EmailNotificationSender
    - NotificationService
      - depends on NotificationSender
- `Constructor injection`
  - Dependencies are explicit
  - Required dependencies cannot be forgotten
  - Fields can remain final
  - Objects are easier to test
  - Invalid objects are harder to construct.
- Setter injection can be useful for optional dependencies
  - the object ay exist before the dependency is assigned
  - For required dependencies, constructor injection is generally clearer.  

- `Field Injection`
  - `@Autowired `  
     `private NotificationSender sender;`
  - It is concise, but it hides dependencies and makes simple unit testing less convenient.
  - Spring also supports annotation-based configuration
  - uses container post-processors to interpret dependency-injection annotations.
- Bean lookup by name
  - `NotificationService service = context.getBean(NotificationService.class);`
  - `Object service = context.getBean("notificationService");`
  - Type-based is usually safer because it avoids string-typing errors.
- Multiple Implementations
  - Possible solutions
    - Giving one bean `@Primary`
    - Using `@Qualifier`
    - Injection a collection of implementations
    - Redesigning the configuration
- id we compile with `java -cp "target/classes" com.learning.spring.SpringDemo` 
  - Maven dependencies are not automatically included
  - JVM only sees target/classes, so Spring is missing
  - use:
    - mvn org.codehaus.mojo:exec-maven-plugin:java -Dexec.mainClass=com.learning.spring.SpringCoreDemo


```text
Spring's IoC container creates and manages beans.
Dependency Injection supplies collaborators from outside the class, usually through constructor
@Configuration defines configuration and @Bean registers objet with the container.
```