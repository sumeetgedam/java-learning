# Lesson 16: Nested classes, Enums, and Records

## Questions

1. What is a static nested class?
2. What is an inner class?
3. What is the difference between a nested class and an inner class?
4. When would you use an enum?
5. Can you create an enum object with `new`?
6. What methods does a record provide automatically?
7. What is a compact record constructor?
8. Are records  deeply immutable?
9. When would you use a record instead of a normal class?
10. Why might defensive copy be needed?

## My summary

- Enum has finite set of instances defined by its constants.
- Enum can contain fields, constructors, and methods.
- Enum constructors are private implicitly, cannot create arbitrary enum instance with `new`.
- A record is a compact way to model data-carrying objects.
  - compiler provides
    - Private final fields
    - A constructor
    - Accessor methods
    - `equals()`
    - `hashCode()`
    - `toString()`
- A record's fields cannoot be reassigned, but reference objects may still be mutable

```text
Nested classes organize closely related types.
Enums represent a fixed set of constants.
Records provide concise value-oriented data classes,
but their immutability is shallow when they contain mutable objects
```