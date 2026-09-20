# Lesson 40: Spring JDBC

## Questions

1. What is a DataSource?
2. What does jdbcTemplate do?
3. Why should JdbcTemplate usually be shared?
4. What does JdbcTemplate.update() do?
5. What does JdbcTemplate.query() do?
6. What is a RowMapper?
7. When would you use queryForObject()?
8. What is NamedParameterJdbcTemplate?
9. Why should SQL parameters not be concatenated into strings?
10. What is exception translation?
11. What is a generated key?
12. How does JdbcTemplate participate in transactions?
13. Why should transaction boundaries usually be in the service layer?
14. What is the difference between repository code and service code?
15. What happens when a query returns no rows?

## My summary

- Spring JDBC handles connection management, statement execution, result-set iteration, resource cleanup and exception transalation while you provide SQL and map reuslts
- Traditional JDBC code requires you to manage:
  - Obtain a connection
  - Create a prepared statement
  - Bind parameters
  - Execute SQL
  - Iterate through results
  - Map rows to objects
  - Handle exceptions
  - Clone resources
- Spring's JDBC handles most f this repetitive infrastructure, you still provide
  - SQL statements
  - Parameter values
  - Row-mapping logic
  - Repository behavior
- A DataSource provides a database connections
  - Application --> JdbcTemplate --> DataSource --> Database connection
  - Application commonly use a connection pool behind the DataSource
  - Spring JDBC can participate in transactions managed through the configured DataSource
  - Do not create a new database connection manually for every repository method
  - Configure one DataSource and inject it.
- JDBC Template
  - `JdbcTemplate` is Spring's classic JDBC abstraction
  - Once configured a shared JdbcTemplate can safely be injected into multiple repositories because its state is configuration state rather than conversational state.
  - Spring provides support for embedded databases such as H2, HSQL, and Derby through its JDBC pacjage
- Prepared statements separate SQL structure from parameter values
- A `RowMapper<T>` converts one database row into on Java Object.
- For a query expected to return exactly one object
  - `jdbcTemplate.queryForObject()`
  - Be aware :
    - No rows can result into an exception
    - More than one row can also result in an exception
  - Use `Optional` plus `query` when "not found" is normal
- `JdbcTemplate` participates in Spring-managed JDBC transactions when used with the appropriate transaction manager.
- Named parameter
  - Traditional JDBC uses positional placeholders
    - WHERE name = ? AND email = ?
  - `NamedParameterJdbcTemplate` uses named parameters
    - WHERE name = :name AND email = :email
  - Spring provides `NamedParameterJdbcTemplate` as a wrapper around `JdbcTemplate` that supports named parameters
  - It can make statements with multiple parameters easier to read.
- Spring translates many JDBC exceptions into its unchecked DataAccessException hierarchy
  - This prevents application code frmo depending directly on vendor-specific SQLException subclasses.
    - DuplicateKeyException
    - EmptyResultDataAccessException
    - DataIntegrityViolationException
    - CannotGetJdbcConnectionException
  - The service layer can decide whether to:
    - Translate the exception into a domain exception
    - Retry
    - Return
    - Return a validation Error
    - Roll back
    - Log and rethrow
- Generated Jeys
    - When inserting an identity-generated row, use `KeyHolder`


```text
Spring JDBC uses a configured DataSource and JdbcTemplate to 
simplify connection handling, statement execution, row mapping, cleanup and exception translation.
Repositories contain SQL, while services coordinate business operations and define transaction boundaries.
```