# Lesson 37: Spring Testing

## Questions

1. What is the difference between a unit test and an integration test?
2. What does @SpringJUnitConfig do?
3. What does @ContextConfiguration do?
4. What does @ActiveProfiles do?
5. How can Spring inject dependencies into a test?
6. Why use a test-specific configuration class?
7. How can a fake implementation replace a product dependency?
8. What is context caching?
9. Why can mutable singleton state cause test interference?
10. What does @DirtiesContext do?
11. Why should @DirtiesContext be used carefully?
12. Why should integration tests be named clearly?

## My summary

- Spring's TestContext Framework integrates Spring with Junit Jupiter and can load an `ApplicationContext`, inject beans into tests, and activate test-specific profiles
- Unit vs Integration
  - A unit test tests one class without starting Spring.
    - Advantages
      - Fast Simple
      - Isolated
      - Easy to debug
  - An Integration test verifies that multiple parts work together :
    - Spring configuration
    - ApplicationContext
    - Bean Creation
    - Dependency injection
    - Application behavior
    - Integration tests are slower because they load real framework configuration.
- `@SpringJUnitConfig`
  - It combines JUnit's Spring extension with Spring test context configuration
  - It loads supplied configuration classes for the test.
    - `@SpringJUnitConfig(AppConfig.class)`
    - `@ExtendWith(SpringExtension.class)`
      `@ContextConfiguration(classes = AppConfig.class)`
    - Spring's JUnit integration supports dependency injection into test contructors, test methods, and lifecycle methods when configured through the Spring extension.
    - For larger tests, constructor injection is often easier to understand than many hidden field dependencies.
- `@ContextConfiguration`
  - Use `@ContextConfiguration`, when you want to explicitly specify the configuration used to load  the application context
  - `ContextConfiguration(classes = AppConfig.class)`
  - The `classes` attribute normally points to `@Configuration` classes, but component classes can also be used.
- Testing scanned components
  - this verifies more than the service;s business logic
    - The configuration loads
    - Component scanning finds the service
    - Its dependencies are available
    - Constructor injection succeeds
- Testing with active profile
  - Use @ActiveProfiles to activate profiles for an integration test.
  - When a test declares @ActiveProfile, the TestContext Framework uses the profiles specific aon the annotation rather than automatically taking them from the spring.profiles.active system property
  - If tests need dynamic profile selection, use an `ActiveProfileResolver`
- Context caching
  - Sprig can cache an ApplicationContext when multiple tests use the same configuration.
  - This reduces startup time across a test suite.
  - Tests may receive the same cached context when they have matching configuration such as :
    - Configuration classes
    - Active Profiles
    - Property sources
    - Context Customizers
  - Avoid mutating singleton bean during tests, shared context state can cause tests to influence one another
- `@DirtiesContext`
  - If a test changes the context in a way that later tests should not observe, use @DirtiesContext
  - This tells Spring that the context should be removed from the cache and rebuilt when needed.
  - Use if carefully, rebuilding contexts can make test suites much slower
  - Prefer resetting the specific test state when possible.

```text
Unit tests test classes directly, while Spring integration tests load an application context and verify configuration,
bean creation, dependendy injection, and selected profiles.
@SpringJUnitConfig loads Spring configuration, and @ActiveProfiles selects the environment used by the test.
```