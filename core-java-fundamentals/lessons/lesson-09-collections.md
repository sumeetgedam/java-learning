# Lesson 9: Collections Framework

## Questions

1. Why would you use a collection instead of an array?
2. What is the difference between `List`, `Set`, and `Map`?
3. Can a `List` contain duplicates?
4. Can a `Set` contain duplicates?
5. What happens when an existing key is inserted into a `Map`?
6. What is the difference between `HashSet`, `LinkedHashSet`, and `TreeSet`?
7. What is the difference between `HashMap`, `LinkedHashMap`, and `TreeMap`?
8. Why should `equals()` and `hashCode()` agree?
9. What is the purpose of `getOrDefault()`?
10. Why should variables generally use interfaces types such as `List` and `Map`?

## My summary

- `List`
  - Preserves insertion order
  - Allows duplicate
  - Supports index-based access
  - `List.of()` creates an immutable list
- `Set`
  - Does not allow duplicates
  - Usually does not guarantee insertion order
  - `HashSet` : No guaranteed order
  - `LinkedHashSet` : Insertion order
  - `TreeSet` : Sorted order
- `hashCode()` to locate a bucket
- `equals()` to compare objects
- If you override `equals()`, you should also override `hashCode()`
- Choosing a collections
  - `ArrayList` : Ordered values, duplicates allowed
  - `HashSet` : Unique values
  - `LinkedHashSet` : Unique values in insertion order
  - `TreeSet` : Sorted unique values
  - `HashMap` : key-value lookup
  - `LinkedHashMap` : Key-value lookup in insertion order
  - `TreeMap` : Sorted keys

```text
`List` is used for ordered values that may contain duplicates.
`Set` is used for unique values, and `Map` is used for key-value lookup.
Hash-based collections depend on a correct `equals()` and `hashCode()` contract.
```