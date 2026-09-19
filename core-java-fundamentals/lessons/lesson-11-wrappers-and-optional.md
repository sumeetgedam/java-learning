# Lesson 11: Wrappers and Optional

## Questions

1. Why do collections use wrapper types instead of primitives?
2. What is autoboxing?
3. What is unboxing?
4. Why can unboxing cause NullPointerException?
5. Why should `==` not be used for wrapper value comparison?
6. What is the difference between `of()` and `ofNullable()`?
7. What is the purpose of `orElse()`?
8. What is the purpose of `orElseGet()`?
9. What is the purpose of `orElseThrow`?
10. When is `Optional` useful?

## My summary

- Wrapper classes
  - `byte` : `Byte`
  - `short`: `Short`
  - `int` : `Integer`
  - `long` : `Long`
  - `float` : `Float`
  - `double` : `Double`
  - `char` : `Character`
  - `boolean` : `Boolean`
- Autoboxing : `Primitive -> wrapper`
- Unboxing : `Wrapper -> primitive`
- `Optional<T>` represents a value that may or may not exist.
- `Optional` variable itself should generally not be null
- Avoid Optional
  - Class fields in ordinary domain objects
  - Method parameters
  - Collections such as `List<Optional<String>>` unless there is a specific reason


```text
Wrapper classes allow primitive values to be used as Objects.
Autoboxing and unboxing converts automatically, but unboxing null causes a `NullPointerException`.
`Optional` represents a value that may be absent without directly returning null.
```