# Lesson 23: Annotations and Reflection

## Questions

1. What is an annotation?
2. What does `@Override` provide?
3. What is annotation retention?
4. What is difference between SOURCE, CLASS, and RUNTIME retention?
5. What does `@Target` control?
6. What is reflection?
7. How can you inspect methods at runtime?
8. Hoe can you read an annotation using reflection?
9. Why do frameworks use annotations and reflection?
10. What are the disadvantages of reflection?

## My summary

- An `annotation` adds metadata to code.
  - Annotation do not usually change the code directly
  - Tools, compilers, and frameworks can read them and take action.
- Common annotations
  - `@Override` : Compiler verifies that the method actually overrides a parent method
  - `@Deprecated` : Compiler warns callers that the method should not be used.
  - `@SuppressWarnings` : Suppresses a specific compiler warning
  - `@FunctionalInterface` : Compiler verifies the interface has exactly one abstract method
- Common retention policies :

| Policy  | Available during           | 
|---------|----------------------------| 
| SOURCE  | Source code only           | 
| CLASS   | Compiled bytecode          | 
| RUNTIME | Runtime through reflection | 

- Use `@Target` to restrict where an annotation can be applied.
- `Reflection` allows a program to inspect classes, method, fields, constructors, annotations at runtime.
- Reflection can : 
  - Make code harder to understand
  - Move errors from compile time to runtime
  - Reduce type safety
  - Complicate refactoring
  - Add overhead
  - Break encapsulation if misused.

```text
Annotations add metadata to code.
Reflection allows a program to inspect and use that metadata at runtime.
Frameworks use both mechanisms to discover components and configure behavior,
but ordinary application code should prefer type-safe direct calls.
```