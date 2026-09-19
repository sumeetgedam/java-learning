# Lesson 21: Junit Testing

## Questions

1. What is a unit test?
2. What do Arrange, Act, and Assert mean?
3. What does `@Test` do?
4. When would you use `assertThrows()`?
5. Why should tests be independent?
6. Why should test names describe behavior?
7. When should you use `@BeforeEach`?
8. Why is exact floating-point comparison risky?
9. What are parameterized tests?
10. What kinds of boundary cases should be tested?


## My summary

- A unit test verifies a small, isolated piece of behavior, usually a method or class.
- Arrange --> Act --> Assert
- Prefer assertions that clearly express the expected behavior.

```text
A unit test verifies one behavior in isolation.
A good test clearly prepares data, invokes the behavior,
verifies the result, and remains independent of other tests.
```