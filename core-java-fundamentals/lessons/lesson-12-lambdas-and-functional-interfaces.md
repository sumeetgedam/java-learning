# Lesson 12: Lambdas and Functional Interfaces

## Questions

1. What is lambda expression ?
2. What is a functional interface?
3. What does `@FunctionalInterface` do?
4. What is the difference between `Predicate`, `Consumer`, `Function`, and `Supplier`?
5. What is a method regerence?
6. What does "effectively final" mean?
7. Where can lambdas be used with collections?
8. When might a normal method be clearer than lambda?

## My summary

- A lambda is a concise way to represent behavior.
- A functional interface has exactly one abstract method.
- `Predicate<T>` : Accepts a value and returns boolean
- `Consumer<T>` : Accepts a value and returns nothing
- `Function<T, R>` : Accepts one type and returns another
- `Supplier<T>` : Takes no input and produces a value
- Method reference is a shorter lambda when an existing method already matches
- A lambda can access local variables only if they are final or effectively final

```text
A lambda represents behavior that can be passed as a value.
Functional interfaces provide the target type for lambdas
`Predicate` tests, `Consumer` performs an action,
`Function` transform a value, and `Supplier` produces a value
```