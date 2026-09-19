# Lesson 6: Inheritance and Polymorphism

## Questions

1. What does `extends` mean?
2. What does `implements` mean?
3. What is method overriding?
4. What is polymorphism?
5. What is the purpose of `@Override`?
6. What is an abstract class?
7. What is an interface?
8. Can Java support multiple class inheritance?
9. What is the difference between an `is-a` and a `has-a` relationship?
10. Why is composition often preferred over inheritance?

## My summary

- `@Override` tells the compiler that you intend to override a parent method
- It helps detect mistakes such as incorrect method names or params
- Polymorphism means one parent-type reference can refer to different child objects
- An abstract class represents an incomplete concept
- A subclass must implement the abstract method, we cannot create an object directly from an abstract class.
- Java does not support multiple class inheritance // class Child extends ParentOne, ParentTwo
- But a class can implement multiple interfaces
- Composition means one class contains another object
- Composition represents a `has-a` relationship // Car has a Engine
- Inheritance represents an `is-a` relationship // Developer is an Employee
- Prefer composition when the relationship is not clearly an `is-a` relationship.

```text
Inheritance models an `is-a` relationship.
Polymorphism allows parent-type references to work with different child implementations.
Interfaces define contracts, while composition models `has-a` relationships.
```