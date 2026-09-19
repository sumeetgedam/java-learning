# Lesson 36: Qualifiers and Profiles

## Questions

1. Why does Spring need @Primary?
2. What does @Qualifier do?
3. When would a custom qualifier annotation be useful?
4. How can a service receive all implementations of an interface?
5. What is a Spring profile?
6. What does @Profile do?
7. How can profiles be activated programmatically?
8. How can profiles be activated from the command line?
9. What is the default profile?
10. What is the Environment abstraction?
11. Why should development and product implementation be separated?
12. What is the difference between @Primary and @Qualifier?

## My summary

- Spring uses qualifier and primary candidates to resolve multiple beans of the same type.
- Profiles allow different bean definitions to be registered only in selected environments
- Multiple bean problem
  - suppose two classes implement the same interface
  - Spring finds two candidates
  - The application context cannot determine which one to inject
- `@Primary`
  - mark one bean as the default
  - when several beans match a single-valued dependency, Spring gives preference to the primary candidate.
  - Use @Primary when one implementation is the normal or preferred choice
- `@Qualifier`
  - Use @Qualifier when a class needs a specific implementation
  - A qualifier narrows the set of type-matching candidates
  - it is not simply a replacement for every bean-name lookup
- Custom qualifier annotations
  - String qualifier are convenient, but custom annotations can be clearer.
  - Custom qualifier are useful when the same selection concept appears in multiple places.
- Injecting all implementations
  - sometimes a service should use every implementation
  - Inject a list or inject a Map
  - The map keys are typically the Spring bean names.
- Ordering injected beans
  - If processing order matters, use `@Order`
  - The list can be ordered according to Spring's ordering rules
  - Do not rely on incident classpath or registration order when the order is meaningful, make the ordering explicit.
- Profile
  - A profile is a named logical group of bean definitions.
    - development
    - test
    - production
  - A bean assigned to a profile is registered only when that profile is active
  - Profiles are part of Spring's Environment abstraction
- `@Profile`
  - only the implementation for the active profile is registered
  - Set active profiles before `refresh()` s Spring can use them while registering bean definitions
  - Profiles can also be activated declaratively through spring.profiles.active.
  - Instead of annotating individual components, annotate configuration classes.
  - Only the configuration matching the active profile contributes its bean.
  - You cna place @Profile on individual @Bean methods
    - This is useful when most configuration is shared but one bean differs by environment
- Activating a profile with JVM property
  - `java -Dspring.profiles.active=development -cp target/classes com.learning.spring.ProfileDemo`
  - multiple profiles can be active : -Dspring.profiles.active=development,local 
  - Spring supports profile expressions with `!`, `&`, and `|`, subject to expression syntax rules
    - `@Profile("production & us-east")`
- Spring has a default profile named : `default`
  - It is active when no other profile is active
  - If any explicit profile is activated the default profile does not apply unless configured otherwise
  - The default profile name can be changed through the environment or spring.profiles.default.
- Environment properties
  - Spring's `Environment` also provides access to properties from sources such as :
    - Properties files
    - JVM system properties
    - Environment variables
    - Servlet context parameters
    - Programmatically supplied properties.


```text
@Primary identifies the preferred bean, while @Qualifier selects a specific candidate
Profiles let Spring register beans for different environments, and Environment exposes active profiles 
and configuration properties.
```