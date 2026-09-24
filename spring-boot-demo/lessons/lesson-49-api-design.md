# Lesson 49 : REST API Design

## Questions

1. Why should API paths usually represent resources?
2. What is the difference between PUT and POST ?
3. What does idempotent mean?
4. Why are idempotency keys useful for payments?
5. When should an API return 201?
6. What is the difference between 401 and 403?
7. When should 409 be used?
8. Why should APIs use separate request and response DTOs?
9. What makes pagination stable?
10. What is the difference between offset and cursor pagination?
11. What are common API versioning strategies ?
12. What is content negotiation?
13. What does OpenApI describe?
14. Wht should API documentation include?
15. Why should API behavior be tested as a contract?
16. Why should client-provided sorting be restricted?
17. What is the purpose of the Location header?

## My summary

- A good REST API makes resource behavior predictable for clients, proxies, and future developers
- Model resources, not actions
  - prefer nouns in URL : 
    - /users
    - /users/35
    - /orders
    - /orders/100/items
  - Avoid action-heavy URLs :
    - /getUsers
    - /createUsers
    - /deleteUSer
  - The HTTP method communicates the operations
    - GET /users -> list users
    - GET /users/42 -> get one user
    - POST /users -> create a user
    - PUT /users/42 -> replace a user
    - PATCH /users/42 -> partially update a user
    - DELETE /users/42 -> delete a user
- Choose HTTP methods deliberately
  - GET
    - Should retrieve a representation without causing an intentional state change
  - POST
    - usually asks the server to process content and create a subordinate resource
  - PUT
    - usually replaces the target resource at a known URI
  - PATCH
    - Partially modifies a resource
  - DELETE
    - Removes or deactivates the target resource.
  - HTTP semantics define PUT, DELETe , and safe methods as idempotent, while POST is not inherently idempotent
- Idempotency
  - An operation is idempotent when repeating the same request has the same intended server effect as making it once.
  - Clients should not automatically retry non-idempotent request unless the API provides a way to make the operation safely repeatable
- Idempotency keys
  - for operations such as payments, orders, or account creation, support an idempotency key
  - the server stores the result associated with the key
  - in production, account for :
    - Concurrent requests with the same key
    - Key expiration
    - Request body mis-match for reused keys
    - Failed vs completed operations
    - Transaction boundaries
- Status Code
  - Successful responses
    - 200 OK
      - Use for a successful read or update with a response body
    - 201 CREATED
      - use when a new resource is created.
    - 204 NO CONTENT
      - Use when the operation succeeds without response body
  - Client errors
    - 400 BAD REQUEST
      - Malformed or invalid input
    - 401 UNAUTHORIZED
      - No valid authentication was provided
    - 403 FORBIDDEN
      - The caller is authenticated but lacks permission
    - 404 NOT FOUND
      - the requested resource does not exist.
    - 409 CONFLICT
      - the request conflicts with current resource state, such as duplicate email.
    - 422 UNPROCESSABLE CONTENT
      - the syntax is valid, but semantic validation fails
  - Server errors
    - 500 INTERNAL SERVER ERROR
      - Unexpected server failure
- When creating a resource, return its location
- Avoid using one unrestricted DTO for every operation
  - Client should not be able to set fields such as :
    - Database IDs
    - Administrative flags
    - Creation timestamps
    - Internal status values
    - Audit fields
- API versioning
  - commonly :
    - /api/v1/users
    - /api/v2/users
  - Header versioning
    - Accept: application/vnd.learning.user-v2+json
  - Query-parameter versioning
    - /users?version=2
- Backward compatibility
  - Prefer additive changes:    
    - Add optional response field
    - Add new endpoint
    - Add optional request field
  - Riskier changes :
    - rename a response field
    - change a field's type
    - remove an endpoint
    - make an optional field required
    - change meaning of an existing status
  - When a breaking change is necessary :
    - Publish a new version
    - Document migration steps
    - Support both versions temporarily
    - Measure usage of the old version
    - Announce and enforce a retirement date
- OpenAPI
  - it is a standard format for describing HTTP APIs, including
    - Paths
    - Methods
    - Parameters
    - Request bodies
    - Responses
    - Schemas
    - Authentication requirements


```text
A well-designed REST API models resources clearly, uses HTTP methods and status codes consistently, 
supports safe retries where needed, exposes stable pagination contracts, separates DTOs from persistence models,
and documents authentication, validation, responses, and errors through an API specification.
```