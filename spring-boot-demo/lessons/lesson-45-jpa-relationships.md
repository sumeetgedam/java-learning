# Lesson 45: JPA Relationships

## Questions

1. What does @ManyToOne represent?
2. What does @OneToMany represent?
3. Which side usually owns a foreign-key relationship?
4. What does mappedBy mean?
5. Why must both sides of a bidirectional relation be synchronized?
6. What does CascadeType.ALL do?
7. When is orphanRemoval appropriate?
8. What is the difference between LAZY and EAGER fetching?
9. What is the N+1 query problem?
10. How can JOIN FETCH help?
11. What is an entity graph?
12. Why can bidirectional entities cause JSON recursion?
13. When should a many-to-many relationship become an explicit join entity?
14. Why should cascade delete be used carefully?
15. Why can collection fetch joins be problematic with pagination?

## My summary

- Relational databases connect tables with foreign keys :
  - In JPA, this relationship can be represented using entity references.
  - `User 1 ---------- * Order`
    - One user can have many orders
- `@ManyToOne`
  - The many-to-one side usually stores the foreign key
- `@OneToMany`
  - A user can expose its orders
- A bidirectional mapping lets both entities navigate to each other
- `CascadeType` : Cascade controls whether operations applied to one entity propagate to related entities.
  - CascadeType.PERSIST
  - CascadeType.MERGE
  - CascadeType.REMOVE
  - CascadeType.REFRESH
  - CascadeType.DETACH
  - CascadeType.ALL
    - operations on the user may cascade to its orders
  - It is appropriate when the child's lifecycle belongs entirely to the parent.
    - OrderLine belongs to Order
  - It may be inappropriate when the related entity is shared.
    - Order reference Product
  - Deleting an order should usually not delete the product.
- `orphanRemoval`
  - true means removing a child from the parent collection can delete the child entity.
- Fetch Strategies
  - supports
    - FetchType.LAZY
    - FetchType.EAGER
  - Lazy fetching
    - The relationship is loaded only when accessed.
    - Advantages :
      - Loads less data initially
      - Avoids unnecessary joins
      - Usually better for collections
    - Risks :
    - Accessing the relationship after the persistence context closes may fail.
  - Eager fetching
    - The relation is loaded immediately
    - can cause :
      - Unnecessary data loading
      - Large joins
      - Unexpected queries
      - Performance problems
- Default fetch behavior

| Relationship  | Default |
|---------------|---------|
| `@ManyToOne`  | EAGER   |
| `@OneToOne`   | EAGER   |
| `@OneToMany`  | LAZY    |
| `@ManyToMany` | LAZY    |

- An entity graph provides another way to specify relationships to fetch
  - `@EntityGraph(attributePaths = "orders")`
  - Use entity graphs when they make the loading requirement clear and reusable
- `@OneToOne`
  - `User 1-------1 Profile`
  - A one-to-one relationship may be better represented by merging tables if the objects always share the same lifecycle
  - Do not create a relationship merely because two concepts are logically related.
- `@ManyToMany`
  - `Student * ------ * Course`
  - A many-to-many relationship reuiqred a join table.

```text
JPA relationships map foreign keys and associations between entities.
The owning side controls the relationship,
,a[[edBy identifies the inverse side and helper methods keep both sides consistent.
Lazy loading, cascade operations, orphan removal and fetch joins must be used deliberately to avoid data and performance problems.
```