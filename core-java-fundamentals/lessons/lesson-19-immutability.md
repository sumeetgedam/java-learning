# Lesson 19: Immutability

## Questions

1. What is an immutable object?
2. Why should immutable fields usually be private and final?
3. Why does final not make the referenced object immutable?
4. What is defensive copying?
5. Why should mutable collections be copied on input?
6. Why should mutable collections be protected on output?
7. What is the difference between List.copyOf and Collections.unmodifiableList?
8. What is shallow immutability?
9. What is deep immutability?
10. Why should immutable update methods return new ojects?

## My summary

- An immutable object cannot change its observable state after construction.
  - Common design :
    - private fields
    - `final` state
    - constructor initialization
    - no setters
    - and defensive handling of mutable values.
- `final` protects the reference, not the object being referenced

```text
Immutability means an object's state cannot change after construction.
`final` protects a reference from reassignment, but defensive copying is needed to protect mutable objects 
and collections reachable through that reference.
```