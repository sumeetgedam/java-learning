# Lesson 18: Access Modifiers and Packages

## Questions

1. What is the default access modifier when no modifier is written?
2. What can `private` members be accessed by?
3. What is package-private access?
4. What is the difference between `protected` and `public`?
5. What does `protected` allow for subclasses in another package?
6. Why should fields usually be private?
7. Why should packages follow the directpry structure?
8. What belongs in `src/main/java`?
9. What belongs in `src/test/java`?
10. Why should public APIs be kept small?
11. What is a package's responsibility?
12. What is a circular dependency?


## My summary
 

| Modifier        | Same class       | Same package         | Subclass in another package | EveryWhere           |  
|-----------------|------------------|----------------------|-----------------------------|----------------------|  
| `private`       | &#10003; allowed | &#10007; not allowed | &#10007; not allowed        | &#10007; not allowed |  
| Package-private | &#10003; allowed | &#10003; allowed     | &#10007; not allowed        | &#10007; not allowed |  
| `protected`     | &#10003; allowed | &#10003; allowed     | &#10003; allowed*           | &#10007; not allowed |
| `public`        | &#10003; allowed | &#10003; allowed     | &#10003; allowed            | &#10003; allowed     |

- Package-private means no modifier is written, only accessible withing same package
- A subclass in another package can access a protected member through inheritance, but not generally through an unrelated parent-object reference.
- Use `private` to protect internal implementation details

```text
Access modifiers control visibility.
Packages organize related code and provide a boundary for package-private access.
A well-organized project exposes a small public API and keeps implementation details private.
```