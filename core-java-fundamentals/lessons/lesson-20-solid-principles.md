# Lesson 20: SOLID Principles

## Questions

1. What does SRP mean?
2. What is a class's "reason to change"?
3. How does OCP reduce modification of existing code?
4. What does LSP mean?
5. Wh re UnsupportedOperationException implementations often suspicious?
6. What is ISP?
7. Why are small interfaces useful?
8. What des DIP mean?
9. Why should high-level code depend on abstractions?
10. How does constructor injection support DIP?

## My summary

- S : Single Responsibility Principle
  - A class should have one reason to change
  - A class does not need to contain only one method, it should have one cohesive responsibility
- O : Open / Close Principle
  - Software should be open for extension but closed for modification
- L : Liskov Substitution Principle
  - A subtype should be usable wherever its parent type is expected.
  - Do not force subclasses to support behaviour they cannot meaningfully provide.
- I : Interface Segregation Principle
  - Clients should not be forced to depend on methods they do not use.
  - Prefer small, focused interfaces
- D : Dependency Inversion Principle
  - High-level code should depend on abstractions, not concrete implementations.


```text
SOLID principles encourage focused classes, stable abstractions,
substitutable implementations, small interfaces, and dependency injection.
They are guidelines, not rules to apply mechanically to every class.
```