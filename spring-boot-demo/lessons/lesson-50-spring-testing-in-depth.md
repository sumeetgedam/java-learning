# Lesson 50: Spring Testing in Depth

## Questions

1. What does MockMvc test?
2. What is the difference between MockMvc and a real HTTP test?
3. What does @WebMvcTest load?
4. What does @SpringBootTest load?
5. What are the Spring Boot web environment modes?
6. What is a test slice?
7. When should @DataJpaTest be used?
8. When should @JdbcTest be used?
9. Why are service unit tests often better without Spring?
10. What does Testcontainers provide?
11. Why can Testcontainers and context caching interact unexpectedly?
12. What is the difference between a test transaction and a server-side transaction?
13. Why should tests not depend on execution order?
14. Why should production databases not be used by automated tests?
15. When should a test use MockMvc versus TestRestTemplate?
16. Why should negative and security cases be tested?
17. What does @ActiveProfile("test") provide?


## My summary

- Spring Boot provides specialized testing support for application contexts,  MVC controllers, JDBC, JPA and full integration tests.
- Testing pyramid
  - A healthy application usually has more fast tests than slow tests :
    - End-to-end tests
    - Full application integration
    - Controller and repository
    - Service unit tests
    - Pure unit tests
  - Unit tests
    - Test one class without Spring.
    - Fast and isolated
  - Slice tests
    - Load only one application layer
      - @WebMvcTest
      - @DataJpaTest
      - @JdbcTest
    - Faster than loading the entire application
  - Integration tests
    - Load the application context :
      - @SpringBootTest
    - useful for verifying real bean wiring and infrastructure
  - End-to-End tests
    - Start the application server and use a real HTTP client
- MockMvc
  - It tests Spring MVC request handling with mock request and response objects rather than starting a real HTTP server
  - It exercises MVC infrastructure such as :
    - Request mapping
    - Argument binding
    - Validation
    - Controller advice
    - Message conversion
    - Security filters, when configured
- WebMvcTest
  - use it when focusing on the web layer
  - It generally loads:
    - MVC configuration
    - Controllers
    - Controller advice
    - Converters
    - Filters
    - Argument resolvers
- SpringBootTest
  - use it when we need the complete Spring Boot application
  - it does not start real server, uses a mock web environment when web support is available
  - web environments
    - Mock environment
      - @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
      - Loads a web application context without starting an embedded server
    - Random port
      - Starts the embedded server on a random port
    - Defined port
      - Starts the server on the configured port
    - No web environment
      - Starts the application context without web infrastructure
- MockMvc vs real HTTP
  - MockMvc
    - Fast
    - No real server
    - Tests Spring MVC processing
    - Useful for most controller contracts
  - Real server
    - Slower
    - Starts embedded server
    - Tests actual HTTP stack
    - Useful for end-to-end behavior
- DataJpaTest
  - use it for repository and JPA mapping tests
  - typically configures :
    - Entity scanning
    - Spring Data repositories
    - JPA infrastructure
    - An embedded database when available
- JdbcTest
  - use it for JDBC components
  - to test :
    - SQL statements
    - Row mappers
    - JDBC repositories
    - Database constraints 
    - Schema scripts
- Many spring test transactions rollback at the end of each test method by default
- TestContainers
  - these runs external services in ocker containers and integrates with JUnit and Spring Boot
  - It is useful when tests need a real backend such as : 
    - PostgreSQL
    - MySQL
    - MongoDB
    - Kafka
    - Redis
    - ElasticSearch

```text
User unit tests for business logic, slice tests for focused Spring layers, MockMvc for MVC contracts without a real server.
@SpringBootTest for application wiring and Testcontainers when realistic infrastructure is required.
A string test suite isolates state, tests failure paths, and avoids loading more framework
infrastructure than necessary.
```