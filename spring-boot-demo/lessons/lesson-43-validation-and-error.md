# Lesson 43: Validation and Error Handling

## Questions

1. Why should request input be validated?
2. What is a DTO
3. Why should API DTOs be separate from database entities?
4. What does @Valid do?
5. What is the difference between @NotBlank and @NotNull?
6. What is MethodArgumentNotValidException?
7. What is HandlerMethodValidationException?
8. What does @RestControllerAdvice do?
9. What does @ExceptionHandler do?
10. Why should APIs return consistent error responses?
11. What is ProblemDetail? 
12. Why should sensitive implementation details not be returned?
13. What does @Valid do for nested objects?
14. When might validation groups be useful?
15. Why should controllers remain responsible for HTTP concerns while services handle business logic?

## My summary

- Spring MVC supports Bean validation on 
  - @RequestBody
  - @ModelAttribute
  - @RequestPart parameters annotated with
    - @Valid or
    - @Validated
  - Invalid request-body validation produces a `400 BAD REQUEST` response
- Why validate request
  - Never assume that client input is valid
  - without validation, invalid data may reach
    - Business logic
    - Database constraints
    - External services
    - Security-sensitive operations
  - Validation should happen near the API boundary
    - HTTP request
    - Deserialize JSON
    - Validate DTO
    - Call service
    - Persist valid data
- DTO
  - A DTO or Sata Transfer Object, represents data crossing an application boundary
  - Use for:
    - Create requests
    - Update requests
    - Response bodies
  - Avoid exposing persistence entities directly through the API
- Spring Boot automatically enables method validation when a Bean Validation implementation is available.
- Common validation aotations

| Annotation  | Purpose                                      | 
|-------------|----------------------------------------------|
| `@NotNull`  | Value cannot be null                         |
| `@NotBlank` | String cannot be null, empty or whitespace   |
| `@NotEmpty` | Collection or string cannot be null or empty | 
| `@Size`     | Restricts string or cllection size           |
| `@Email`    | Validates email-like format                  |
| `@Positive` | Number must be greater than zero             |
| `@Min`      | Minimum numeric value                        | 
| `@Max`      | Maximum numeric value                        |
| `@Pattern`  | Matches a regular expression                 |

- The annotation describe constraints on incoming data.
- Without `@Valid`, the constraints are declared but request validation is not triggered at this boundary
```text
[learning-app] [nio-8080-exec-1] .w.s.m.s.DefaultHandlerExceptionResolver : 
Resolved [org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in 
public org.springframework.http.ResponseEntity<com.learning.boot.user.User> 
com.learning.boot.user.UserController.create(com.learning.boot.user.CreateUserRequest): 
[Field error in object 'createUserRequest' on field 'email': rejected value [jordan]; 
codes [Email.createUserRequest.email,Email.email,Email.java.lang.String,Email]; 
arguments [org.springframework.context.support.DefaultMessageSourceResolvable: 
codes [createUserRequest.email,email]; arguments []; 
default message [email],[Ljakarta.validation.constraints.Pattern$Flag;@56e5a1bf,.*]; 
default message [Email must be valid]] 
```

- Use @RestControllerAdvice to define handlers shared across controllers.
  - it combines controllers advice behavior with response-body behavior
  - its exception handlers can apply across controllers discovered by Spring.
- Local controller handlers are generally considered before global advice handlers
- Global advice can be narrowed to selected packages, annotations or controller types when necessary.
- Spring supports RFC 9457-style problem details through `ProblemDetail`
  - Problem details provide a standardized structure for HTTP API errors
  - For a project, choose either a custom error DTO or ProblemDetail and use it consistently


```text
DTOs define clear API boundaries, Bean Validation checks incoming data, 
and @RestControllerAdvice centralizes exception handle validation, not-found, 
and conflict responses predictably
```