# Lesson 34: Component Scanning

## Questions

1. What is component scanning?
2. What does @Component mean?
3. What is the purpose of @Service?
4. What is the purpose of @Repository?
5. What is the purpose of @Controller?
6. What does @ComponentScan do?
7. Where does scanning begin when no base package is specified?
8. How are default bean names generated?
9. What happens when multiple beans match one dependency?
10. What does @Primary do?
11. What does @Qualifier do?
12. What is a composed stereotype annotation?


## My summary

- Spring can discover classes annotated with component stereotypes and register them as beans automatically.
- By default, component scanning detects 
  - `@Component`
  - `@Repository`
  - `@Service`
  - `@Controller`
  - `@Configuration`
  - and custom annotations built on `@Component`
- Spring discovers the class and registers it automatically when its package is scanned
  - @ComponentScan
  - discover annotated classes
  - register bean definitions
  - create and wire objects.
- `@Component`
  - It is the general-purpose stereotype for a Spring-managed class.
  - Use @Component when the class does not fit a more specific role
- `@Service`
  - Use @Service for application or business logic
  - @Service is a specialization of @Component intended for service-layer classes.
- `@Repository`
  - Use @Repository for persistence or data-access classes.
  - @Repository identifies a data-access component and can participate in Spring's persistence exception-translation mechanisms when configured appropriately.
- `@Controller`
  - Use @Controller for presentation layer components, especially Spring MVC controllers.
  - @Controller is also a specialization of @Component
- Stereotype hierarchy
  - @Component
    - @Service
    - @Repository
    - @Controller
    - custom composed annotations
  - The specialized annotations communicate intent :

| Annotation    | Typical responsibility           | 
|---------------|----------------------------------|
| `@Component`  | Generic Spring-managed component |
| `@Service`    | Business/application logic       |
| `@Repository` | Data Access                      |
| `@Controller` | Presentation / web layer         |

- Use `@ComponentScan` to enable component scanning
  - `@ComponentScan("com.learning.spring")`
  - if no package is specified, scanning begins recursively from the package containing the configuration class.
  - Avoid scanning overly broad packages : `@ComponentScan("com")`
  - Prefer the narrowest application root package
- Package structure
  - com.learning.spring/
    - SpringCoreDemo.java
    - config/
      - AppConfig.java
    - notification/
      - NotificationSender.java
      - ConsoleNotificationSender.java
      - NotificationService.java
    - repository/
      - UserRepository.java
    - controller/
      - UserController.java
  - If `AppConfig` scans `com.learning.spring`, it can discover components in its subpackages.
- Default bean names
  - Spring commonly derives a bean name frmo the class name.
    - NotificationService -> notificationService
- `@Primary`
  - Mark one implementation as the default
- `@Qualifier`
  - Use @Qualifier, when you want a specific implementation
- Custom composed annotations
  - Spring supports custom annotations built using `@Component` as  ameta-annotation
  - Such annotations can make classes eligible for component scanning.
  - This can make architectural roles clearer, but fdo not create custom annotations unnecessarily.

```java
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface  UseCase {
    
}

@UseCase
public class RegisterUserUseCase {
    
}
```

```text
Component scanning discovers classes marked with Spring stereotypes
and registers them as beans: @Service, @Repository, @Controller
communicate the role of a class, while constructor injection connects discovered beans.
```