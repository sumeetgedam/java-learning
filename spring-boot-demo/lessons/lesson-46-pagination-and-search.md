# Lesson 46: Pagination and Dynamic Search

## Questions

1. Why is pagination important?
2. What does Pageable represent?
3. What is the difference between PAge and Slice?
4. Why can Page require an expensive count query?
5. How are page and size supplied through HTTP requests?
6. Why should page size be limited?
7. Why must client-provided sort properties be validated?
8. What is JpaSpecificationExecutor?
9. Why are specifications useful for optional filters?
10. What is the difference between derived queries and specifications?
11. Why should pagination use a stable sort order?
12. What is offset pagination?
13. What is keyset pagination?
14. When is cursor pagination preferable?
15. What is Query by Example?

## My summary

- Spring Data repositories support pagination and sorting through 
  - `Pageable`
  - `Page`
  - `Slice`
  - `Sort`
- A Page includes total-result metadata and usually requires a count query
- A Slice only determines whether another slice exists.
- For a table with millions of rows, returning every row can cause :
  - High memory usage
  - Slow database queries
  - Large HTTP responses
  - Long response times
  - Application instability
- Pageable describes:
  - Page number
  - Page size
  - Sort order
- The Pageable parameter allows the caller to control the requested page and sort order.
- Page
  - Use Page<T> when the client needs total-result information :
    - `Page<User> page = userRepository.findAll(PageRequest.of(0, 20))`
    - conceptually Page :
      - Current records
      - current page number
      - page size
      - total elements
      - total pages
    - useful methods :
      - page.getContent()
      - page.getNumber()
      - page.getSize();
      - page.getTotalElements()
      - page.getTotalPAges()
      - page.hasNext()
      - page.hashPrevious()
      - page.isFirst()
      - page.isLast()
  - A Page generally requires a count query to calculate total elements and toal page
  - This count query can be expensive for complex or large datasets
- Slice
  - User Slice<T> when the client only needs to know whether more data exists :
  - `Slice<User> slice = userRepository.findByNameContainingIgnoreCase("alex", PageRequest.of(0, 20));`
  - A slice does not need to calculate the complete number of matching rows
  - USe slice for interfaces :
    - Load more
    - Infinite scrolling
    - Batching processing
    - Next-page navigation
  - useful methods : 
    - slice.getContent()
    - slice.hashNext()
    - slice.hasPrevious()
    - slice.getNumber()
    - slice.getSize()

| Requirement                             | Use      |
|-----------------------------------------|----------|
| Need total number of results            | Page<T>  | 
| Need total number of pages              | Page<T>  |
| Need only current records               | Slice<T> |
| Need to know whether more records exist | Slice<T> |
| Avoid an expensive count query          | Slice<T> |

- Spring MVC can bind request parameters to Pageable 
  - The default page index is zero-based.
  - Spring Data's default web binding uses 
    - page
    - size
    - sort
  - the default page size is commonly 20 unless customized
- A production API should also validate :
  - Maximum page size
  - Allowed sort properties
  - Maximum number of sort fields
  - Default sort order
- Do not trust arbitrary sort properties
  - Clients should not be allowed to sort by arbitrary internal fields or expressions.
  - Applications are responsible for validating sort properties arriving from request parameters.
- Service-layer pagination:
  - `Page.map(...)` transforms entities into response DTOs while preserving pagination metadata.
  - returning Spring's Page directly cna expose framework=specific response details, a custom response gives us more control.
- Simple filtering can use derived methods :
  - `Page<User> findByNameContainingIgnoreCase`
  - `Page<User> findByRmailContainingIgnoreCase`
  - `Page<User> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase`
- `JpaSpecificationExecutor` allows repository methods to execute reusable predicate built with the JPA Criteria API

```text
Pageable combines page, size, and sorting information. Page provides total result metadata,
while Slice avoids the count query when only next page information is needed.
Specifications compose optional filters, and large data APIs may benefit from keyset or cursor pagination
```