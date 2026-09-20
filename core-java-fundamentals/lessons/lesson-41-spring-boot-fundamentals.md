# Lesson 41: Spring Boot Fundamentals

## Questions

1. What problem does Spring Boot solve?
2. What does @SpringBootApplication combine?
3. What is auto-configuration?
4. Why can auto-configuration back off?
5. What is a Spring Boot starter?
6. What is an embedded server?
7. What is the default HTTP port?
8. How can the server port be changed?
9. What is externalized configuration?
10. What is the difference between @Value and @ConfigurationProperties?
11. What are Spring Boot profiles?
12. What is Spring Boot Actuator?
13. What is the typical application startup flow?
14. How can a web server be disabled?
15. Why should secrets not be commited to application.properties?

## My summary

- Spring Boot simplifies Spring application setup by providing :
  - convention-based configuration
  - dependency starters
  - executable application support 
  - automatic configuration based on the classpath and declared beans
- Spring Framework provides features such as :
  - IoC container
  - Dependency injection
  - AOP
  - Transactions
  - JDBC
  - MVC
  - Testing support
- Spring Boot builds on Spring Framework and reduces setup code :
  - Auto-configuration
  - Starter dependencies
  - Embedded servers
  - Externalized configuration
  - Executable packaging
  - Operational features
- Spring boot does not replace Spring, it configures Spring with sensible defaults and helps applications start quickly
- `SpringApplication.run(..)` 
  - starts the application
  - creates the application context
  - registers beans
  - applies auto-configuration
  - and starts the embedded server when the application is configured as web application.
- `@SpringBootApplication`
  - combines : 
    - `@SpringBootConfiguration`
      - Identifies the primary Spring Boot configuration class
    - `@EnableAutoConfiguration`
      - Allows Spring Boot to configure components based on : 
        - Classes present on the classpath
        - Existing beans
        - Application properties
        - Conditional configuration
    - `@ComponentScan`
      - scans the package of the application class and its subpackages by default.
      - put main class near the root
        - allows component scanning to discover application components naturally.
- Auto-configuration
  - Auto-configuration attempts to configure common infrastructure automatically
  - Examples:
    - If its web application, Spring boot can configure web infrastructure
    - If database dependencies and properties are available, it can configure DataSource 
  - Its conditional , commonly checks :
    - Is a required class present?
    - Is a particular bean already defined?
    - Is a property enabled?
    - Is a web application being created?
  - Auto-configuration shouls back off when you provide you own matching configuration.
  - This allows you to override defaults
    - No Custom DataSource
    - Boot configures a DataSource
    - Custom DataSource exists
    - Boot backs off
- Starters
  - A starter is a convenient dependency descriptor that brings together dependencies commonly required for a particular type of application.
  - examples include : 
    - spring-boot-starter
    - spring-boot-starter-webmvc
    - spring-boot-starter-webflux
    - spring-boot-starter-jdbc
    - spring-boot-starter-validation
    - spring-boot-starter-actuator
- Spring boot can run as a standalone application with an embedded web server.
  - java -jar application.jar
  - Spring Boot starts
  - ApplicationContext starts
  - Embedded server starts
  - HTTP requests accepted
- Current Spring boot documentation list embedded servlet support
  - Tomcat 
  - Jetty
  - Reactive application can use
    - Reactor Netty
    - Tomcat
    - Jetty
- Read a property with `@Value`
  - `@Value("${app.name}")` 
  - `@Value("${app.name}")` 
  - it is convenient for one or two values, but `@ConfigurationProperties` is generally better for a related group of configuration values.
- Profiles allow environment specific configuration without  changing source code.
- Actuator
  - Spring Boot Actuator provides production-oriented and management features
  - The Actuator starter is included separately
- A simplified start-up sequence
  - main()
  - SpringApplication.run()
  - Environment created
  - ApplicationContext created
  - Configuration discovered
  - Component scanning
  - Auto-configuration
  - Beans instantiated
  - Embedded server starts
  - ApplicationReadEvent
- If start fails, inspect
  - The first meaningful exception
  - Missing beans
  - Configuration values
  - Port conflicts
  - Dependency conflicts
  - Database connection errors
- For a command line application
  - `spring.main.web-application-type=none`
  - `application.setWebApplicationType(WebApplicationType.NONE)`
  - This allows Spring boot to run without starting an HTTP server.

```text
Spring Boot simplifies Spring application development through 
auto-configuration, starters, embedded servers and externalized configuration.
@SpringBootApplication provides the main configuration, component scanning, and auto-configuration entry point.
```