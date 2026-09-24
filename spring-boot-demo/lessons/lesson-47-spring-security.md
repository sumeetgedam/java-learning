# Lesson 47 : Spring Security Fundamentals

## Questions

1. What is authentication?
2. What is authorization?
3. What is the SecurityFilterChain?
4. What does spring-boot-starter-security provide?
5. What is UserDetailsService?
6. Why must passwords never be stored in plaintext?
7. What does PasswordEncoder do?
8. What is DelegatingPassword Encoder?
9. What is the difference between a role and an authority ?
10. What does permitAll mean?
11. What does authenticated mean?
12. What is the difference between 401 and 403?
13. What does @EnableMethodSecurity do?
14. What is CSRF?
15. Why should CSRF not be disabled without analyzing the authentication mechanism?
16. What is the difference between HTTP Basic and form login?
17. Why should security rules be tested with multiple user roles?

## My summary

- Spring Security provides authentication, authorization and protection against common attacks.
- In a Spring Boot web application, adding Spring security secures the application by default; custom access rules are typically defined with a `SecurityFilterChain` bean
- Authentication vs Authorization
  - Authentication answers : Who are you ?
    - Username and password
    - Session cookie
    - API token
    - OAuth2 login
    - Client certificate
  - Authorization answers : What are you allowed to do?
    - Authenticated users can view profiles
    - Administrators can delete users
    - Managers can approve orders
  - Authentication --> Identity established --> Authorization --> Access decision
  - A user can be Authenticated but still forbidden from accessing a particular resource
- Adding Spring Security may require authentication automatically
  - startup message resembling :
    - User generated security password: ...
- Spring Security processes requests through a chain of servlet filters
  - HTTP request
  - Security filters
    - Authentication
    - CSRF protection
    - Session handling
    - Request authorization
    - Exception handling
  - Controller
- A filter chain can 
  - Extract credentials
  - Authenticate users
  - Restore sessions
  - Reject unauthorized requests
  - Apply CSRF protection
  - Add security headers
  - Continue to controller
- A controller should not manually implement authentication checks for every request.
- Security concerns should normally be handled by Spring Security configuration
- A custom `SecurityFilterChain` bean is thr normal way to override Spring Boot's default web security rules
- Spring Security supports in-memory users, JDBC users, custom data stores and LDAP storage.
- User `PasswordEncoder` to store password.
  - `PasswordEncoderFactories.createDelegatingPasswordEncoder()`
  - Password encoders perform one-way transformations
  - Recommended adaptive password functions include bcrypt, PBKDF2, scrypt, Argon2.
  - Their work factor should be tuned for the application environment
  - `DelegatingPasswordEncoder`
    - Spring Security's delegating encoder stores an algorithm identifier with the encoded password.
    - `{bcrpyt}encoded-value`
    - `{id}encodedPassword`
    - The identifier tells Spring which encoder should verify the stored value
    - The helps applications :
      - Use current password-storage recommendations
      - Support legacy formats during migration
      - Upgrade password encoding over time.
  - Do not use `NoOpPasswordEncoder` for production passwords, plaintext password storage is not secure
- Roles and authorities 
  - A user can have authorities :
    - ROLE_USER
    - ROLE_ADMIN
    - using `.roles("USER", "ADMIN")`
  - Spring adds the ROLE_ prefix internally
  - Authorize with :
    - `hasRole("ADMIN")`
  - Alternatively, define authorities directly :
    - `.authorities("REPORT_READ", "REPORT_EXPORT")`
  - Authorize with : 
    - `.hasAuthority("REPORT_EXPORT")`
  - Role : Broad application grouping
  - Authority : Specific permission
  - For larger applications, fine-grained authorities often describe permission more accurately than a long list of roles.
- Put specific rules before broad rules, broad rule should not appear first.
- HTTP Basic sends credentials through the Authorization header.
  - Authorization : Basic base64(username:password)
  - Basic authentication should be used over HTTPs
  - Credential must not be transmitted over an unencrypted connection.
  - Spring Security's Basic Authentication support uses the WWW-Authenticate challenge when an unauthenticated request requires authentication
- for browser applications : 
  - `http.formLogin(Customizer.withDefaults())`
  - Spring Security can provide a default login page for development
  - Production applications commonly use a custom login page and carefully configure the login flow.
- The Authentication object can expose :
  - authentication.getName()
  - authentication.getAuthorities()
  - authentication.isAuthenticated()
  - authentication.getPrincipal()
- Request-level security protects URL patterns
  - Method security protects service methods regardless of which controller calls them
  - Spring Security supports authorization rules attached to request URIs and methods
  - Method level security is useful when : 
    - Multiple endpoint call the same service
    - Authorization depends on method arguments
    - Business-layer protection is required
    - A resource must be protected regardless of controller mapping
- `@PostAuthorize` evaluates authorization after a method returns : 
  - Use post-authorization cautiously because the method executes before the access decision is completed.
  - For common ownership checks, explicit service logic or query-level filtering may be easier to understand and more efficient
- `CSRF protection`
  - Cross-Site Request Forgery tricks a browser into sending an unwanted state-changing request using the victim's authenticated session.
  - Example risk
    - Victim is logged in
    - Malicious site submits a transfer request
    - Browser automatically sends session cookie
  - Spring Security enables CSRF protection by default for many browser-oriented configurations
  - For a server-rendered form, include the CSRF token as required by the configured integration
  - For a stateless API using bearer tokens in the Authorization header rather than browser cookies, CSRF analysis differs
  - Do not disable CSRF merely because an endpoint is an API; understand the authentication mechanism and browser behaviour first.
- Authentication failures vs authorization failures
  - Unauthenticated
    - The request has no valid authentication
      - 401 UNAUTHORIZED
  - Authenticated but forbidden
    - The user is authenticated but lacks permission
      - 403 FORBIDDEN
- Authentication architecture
  - Request credentials
  - Authentication filter
  - AuthenticationManager
  - AuthenticationProvider
  - UserDetailsService
  - PasswordEncoder.matches(...)
  - Authentication stored in SecurityContext

```text

Authentication establishes identity, while authorization determines access.
Spring Security applies these rules through a filter chain.
SecurityFilterChain configuration, UserDetailsService, and password encoders
Passwords must be stored using adaptive one-way hashing, anf
authorization rules should be explicit and tested.
```