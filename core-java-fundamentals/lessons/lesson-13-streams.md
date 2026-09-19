# Lesson 13: Streams

## Questions

1. What is a stream?
2. What is the difference between an intermediate and terminal operation?
3. What does `filter` do?
4. What does `map` do?
5. What is the difference between `map` and `flatMap`?
6. What does lazy evaluation mean?
7. What does `reduce` do?
8. What is the difference between `findFirst` and `findAny`?
9. When would you use `groupingBy`?
10. Does a stream modify the original collection?

## My summary

- A Stream is a pipeline for processing data, it does not store data itself
  - `source -> intermediate operations -> terminal operation`
- `map` : applies function on every element
- `reduce` : combines multiple values into one result
- `faltMap` : converts nested structures into one stream


```text
A Stream processes data through a pipeline.
Intermediate operations such as filter and map transform the pipeline, 
while operations such as `collect`, `count`, and `reduce` produce a result.
```