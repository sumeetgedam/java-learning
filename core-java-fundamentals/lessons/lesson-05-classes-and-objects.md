# Lesson 5: Classes and Objects

## Questions

1. What is a class?
2. What is an object?
3. What is an instance field?
4. What is a constructor?
5. What does `this` mean?
6. What is encapsulation?
7. Why should fields usually be private?
8. What is the difference between an instance member and a static member?
9. What makes a class immutable?

## My summary

- A `class` is a blueprint
- An `object` is an instance created from that blueprint
- Encapsulation
  - Keep internal state private
  - Expose controlled operations through methods
  - Prevent invalid state
- Use `static` for behavior or data that does not belong to one particular object
- Immutable class 
  - all fields final 
  - Constructor initialization
  - Getters 
  - No setters

```text
A class defines state and behavior.
An object is a runtime instance of that class.
Encapsulation protects the objects internal state by restricting
direct access and exposing controlled operations.
```