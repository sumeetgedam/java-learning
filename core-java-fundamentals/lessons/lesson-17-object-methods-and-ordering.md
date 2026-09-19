# Lesson 17: Object Methods and Ordering

## Questions

1. What is the difference between `==` and `equals()`?
2. What is the `equals()` contract?
3. Why must `hashCode()` be overridden when `equals()` is overridden?
4. What is a hash collision?
5. What is the purpose of `toString()`?
6. What is the difference between `Comparable` and `Comparator`?
7. When would you use natural ordering?
8. Why should mutable objects generally be avoided as map keys?
9. Why should `Double.compare` be preferred over subtracting doubles?
10. what methods do records generate automatically?

## My summary

```text
`equals()` defines logical equality, 
`hashCode()` supports hash-based collections, and 
`toString()` provides readable object output.
`Comparable` defines one natural ordering,  while
`Comparator` supports external or multiple orderings.
```