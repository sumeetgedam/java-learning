# Lesson 42: Spring MVC Controllers

## Questions

1. What is the role of DispatcherServlet?
2. What is the difference between @Controller and @RestController?
3. What does @RequestMapping do?
4. What are @GetMapping and @PostMapping?
5. What is @PathVariable used for?
6. What is @RequestPAram used for?
7. What does @RequestBody do?
8. What is an HttpMessageConverter?
9. When would you use ResponseEntity?
10. What status code represents resource creation?
11. What is the difference between 404 and 400?
12. Why should controllers remain thin?
13. How should JSON body input differ from query-parameter input?
14. How does Spring convert request values to Java types?

## My summary

- Spring MVC provides an annotation-based programming model for controllers
- Controllers use annotations to define request mappings, read request data and produce responses.
- Spring MVC request flow
  - HTTP request
  - Embedded servlet server
  - DispatcherServlet
  - Controller Mapping
  - Response conversion
  - HTTP response
- The `DispatcherServlet` coordinates request processing and delegates to the appropriate controller method.
- `@Controller`
  - Use this for MVC controllers that commonly return view names:
  - commonly used with server-side template engines.
- `@RestController`
  - Use this for HTTP APIs
  - It is effectively a controller whose method return values are written directly to the HTTP response body.
  - A configured HTTP message converter serializes the returned object, commonly as JSON in a Boot web applicatino.
  - Controller method arguments and return values are converted through Spring MVC infrastructure.
- Request mappings
  - You can map endpoints with `@RequestMapping`
  - `@RequestMapping(method=RequestMethod.GET, path="/hello")`
  - `@GetMapping("/hello")`
  - `@PostMapping("/users")`
  - `@PutMapping("/users/{id}")`
  - `@PatchMapping("/users/{id}")`
  - `@DeleteMapping("/users/{id}")`
  - The class-level mapping provides the common prefix
  - The method-level mapping adds the remaining path
- HTTP methods

| Method   | Typical use           |
|----------|-----------------------|
| `GET`    | Read data             |
| `POST`   | Create data           |
| `PUT`    | Replace data          |
| `PATCH`  | Partially update data |
| `DELETE` | Delete data           |

- `@PathVariable`
  - Use this to read a value embedded in the URL.
  - Spring MVC supports @PathVariable as a controller method argument for URI template variables
- `@RequestParam`
  - Use this to read query parameters
  - `GET /users?role=admin`
  - Spring converts request parameter strings to declared argument types where possible
- `@RequestHeader`
  - reads a request header
  - `@RequestHeader("X-Request-ID")`
  - `@RequestHeader(name="X-Client", required = false)`
- `@CookieValue`
  - reads a cookie
  - `@CookieValue(name="theme", required=false)`
  - use cookies for small pieces of client-associated state
  - Avoid putting sensitive information into cookies without appropriate security protections.
- `@RequestBody`
  - use this to deserialize HTTP request body into a Java object
  - Spring uses an HttpMessageConverter to read and deserialize the request body
  - @RequestBody can also be combined with validation annotations.
- `@ResponseBody`
  - In regular `@Controller`, use @ResponseBody when the return value should be written directly to the response body.
  - `@RestController` applies response-body behavior to controller methods by default.
- `@ResponseEntity`
  - use `ResponseEntity` when you need explicit control over :
    - HTTP status
    - Response header
    - Response body
  - Typical response statues: 

| Status                      | Meaning                         | 
|-----------------------------|---------------------------------|
| `200 OK`                    | Successful request              |
| `201 CREATED`               | Resource created                |
| `204 NO CONTENT`            | Successful request with no body |
| `400 BAD REQUEST`           | Invalid client input            |
| `404 NOT FOUND`             | Resource does not exist         |
| `409 CONFLICT`              | State conflict                  |
| `500 INTERNAL SERVER ERROR` | Unexpected server failure       |

- A controller should primarily handle
  - HTTP request
  - Input conversion
  - Service call
  - HTTP response
- Form data should generally be read with @RequestParam, not treated as a JSON request body


```text
Spring MVC maps HTTP requests to controller methods using annotations.
@PathVariable reads values from URL path
@RequestParam reads query parameter
@RequestBody deserializes request Bodies
and @ResponseEntity gives explicit control over HTTP responses.
```