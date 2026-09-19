# Lesson 10: Generics

## Questions

1. Why are generics useful?
2. What problem do generics solve?
3. What does `<T>` represent?
4. What is a generic class?
5. What is a generic method?
6. What does `?` mean?
7. What does `? extends Number` mean?
8. What does `? super Integer` mean?
9. What does PECS mean?
10. Why should raw types be avoided?
11. What is type erasure?

## My summary

- `T extends Number` mean `T` must be `Number` or a subclass.
- `? extends Number`
  - some type that extends `Number`
  - upper bound
  - useful when a method primarily reads values.
- `? super Integer`
  - Integer or one of its parent types
  - Lower bound
  - useful when a method adds values.
- PECS
  - Producer Extends
  - Consumer Super
- Raw types
  - Disable compile-time type checking
  - Require casts
  - Can produce runtime errors
  - Usually indicat legacy code.
- Type erasure
  - Java generics primarily provide compile-time type safety
  - At runtime, generic type information is mostly removed through type erasure.


```text
Generics provide compile-time safety and reduce casting.
`extends` is generally used when reading from a producer, 
while super is generally use when writer to a consumer.
```