# Lesson 44: Spring Data JPA

## Questions

1. What problem does JPA solve ?
2. What is an entity?
3. What do @Entity and @Table do?
4. What do @Id and @GeneratedValue do?
5. Why does a JPA entity need a no-argument constructor?
6. What does a JpaRepository provide?
7. How are derived query methods created?
8. What does Optional communicate?
9. What is a persistence context?
10. What is dirty checking?
11. What is the difference between JPQL and native SQL?
12. Why should transaction boundaries usually be in services ?
13. Why should entities not usually be returned directly from controllers ?
14. What is the N+1 query problem?
15. Why should automatic schema updates be used cautiously ?

## My summary

- JPA is a standard API for mapping Java objects to relational database tables.
- Spring Data JPA builds repository implementations from interfaces and `can derive queries from method names`.
- JDBC vs JPA
  - With JDBC, you write SQL and map rows manually:
    - SQL query
    - ResultSet
    - RowMapper
    - Java Object
  - With JPA
    - Java entity
    - JPA provider
    - SQL generated automatically
    - Database table
- JPA reduces repititive persistence code, but it does not eliminate the need to understand
  - SQL
  - Relationships
  - Transactions
  - Indexes
  - Fetching
  - Query performance
- Spring Boot's JPA starter provides Spring Data JPA, Spring ORM, and a JPA implementation such as Hibernate.
- An Entity is a Java class mapped to a database table
  
| Annotation        | Purpose                       |
|-------------------|-------------------------------|
| `@Entity`         | Marks the class as persistent |
| `@Table`          | Specifies the data table      |
| `@Id`             | Identifies the primary key    |
| `@GeneratedValue` | Configures ID generation      | 

- Spring Boot scans entity classes in its auto-configuration packages.
- JPA requires an accessible no-argument constructor so the persistence provider can create entity instances.
  - The constructor does not need to be public.
  - Keeping it protected prevents most application code from accidentally creating an incompletely initialized entry.
- Common ID Generation Strategy includes :
  - `@GeneratedValue(strategy = Generation.IDENTITY)`
  - `@GeneratedValue(strategy = Generation.SEQUENCE)`
  - `@GeneratedValue(strategy = Generation.AUTO)`
- Do not assume that an ID strategy behaves identically across all database vendors.
- We do not implement a repository manually
  - Spring Data creates a repository implementation and registers it as Spring bean
  - Repository interfaces are commonly derived from 
    - `Repository`, 
    - `CrudRepository` or 
    - `JpaRepository`
- `JpaRepository<Use, Long>` provides operations such as:
  - userRepository.save(user)
  - userRepository.findById(id)
  - userRepository.findAll()
  - userRepository.existsById(id)
  - userRepository.count()
  - userRepository.deleteById(id)
- JpaRepository -> PagingAndSortingRepository -> CrudRepository -> Repository
- JpaRepository<User, Long>
  - User -> entity type
  - Long -> ID type
- Spring Data can derive from method names.
  - findByEmail(String email)
  - findByName(String name)
  - findByNameContainingIgnoreCase(String name)
  - existsByEmail(String email)
  - countByName(String name)
  - Spring Data JPA creates queries from these method names.
- Keep repositories focused on persistence and place business operations in services.
  - The service layer is a natural place to define a transaction spanning multiple repository operations.
- Avoid `repository.findById(id).get()`
  - `get()` throws a generic exception when the value is missing
  - hides the domain meaning of the failure
- JPA takes care f insert / update or merge depending on its persistence state and identifier
- A persistence context is managed set of entity instances
  - within a transaction, an entity can be
    - Transient -> Managed -> Detached -> Removed
    - Transient
      - Created with new but not et managed
      - User user = new User("Alex", "alex@example.com")
    - Managed
      - Associated with the persistence context
      - User saved = repository.save(user)
    - Detached
      - No longer associated with the current persistence context
    - Removed
      - Marked for deletion
  - At transaction completion, JPA can detect the changed field and issue and update
  - For clarity, explicit `save()` is often still used in service coed when the operation's intent benefits from being obvious
- Spring Data JPA supports `@Query` for queries that cannot be expressed cleanly with method-name derivation.
- Use native SQL carefully because it couples the repository to a specific data schema and potentially a specific database vendor.
  - Prefer derived queries or JPQL when the ehy express the requirement clearly
- Database schema generation
  - Use `create-drop` mainly for temporary development or tests
  - For production systems, prefer controlled schema migrations with tools such as Flyway or Liquibase rather than relying on automatic schema mutation
  - Spring Boot exposes JPA and Hibernate configuration through spring.jpa.* and spring.jpa.properties.*
- Avoid returning entities directly from controllers
  - Prefer mapping to a response DTO
  - This helps prevent :
    - Accidental persistence-model exposure
    - Recursive relationship serialization
    - Lazy-loading surprises
    - API shape being coupled to the database model
- Common JPA pitfalls
  - N+1 queries
    - You load one list of parent entities, then execute one additional query for each entity's related data
      - 1 query for users
      - N queries for each user's orders
    - investigate generated SQL and fetch plans
  - Lazy loading outside a transaction 
    - A relationship may be configured for a lazy loading but accessed after the persistence context is closed.
  - Returning entities directly
    - This can expose internal structure or trigger unexpected relationship loading
  - Excessive `EAGER` relationships
    - Eager loading can review more data than needed and create large joins
  - Using ddl-auto=update in production
    - Automatic schema changes can be unsafe and difficult to audit.
  - Ignoring indexes
    - A derived repository method does nt automatically mean the database query is efficient
    - Inspect query plans and add appropriate indexes


```text
JPA maps Java entities to relational data, while Spring Data JPA generates repository implementations and
derives simple queries from method names. Entities belong to the persistence layer, services define business transactions
and controllers should expose STOs rather than persistence objects.
```