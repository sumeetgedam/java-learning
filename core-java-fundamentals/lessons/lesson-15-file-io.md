# Lesson 15: File I/O

## Questions

1. What is a `Path`?
2. What is the purpose of `Files`?
3. What is the difference between a relative and absolute path?
4. When would you use `readString()`?
5. When would you use a `BufferedReader`?
6. Why should file streams be closed?
7. What is try-with-resources?
8. What exception is commonly associated with file operations?
9. What is the difference between copying and moving a file?
10. Why should large files not always be loaded completely into memory?

## My summary

- `Path` represents a filesystem location
- `Files` provides operations on files and directories
- `createDirectories` creates missing parent directories.
- for larger files, use a buffered stream rather than loading every line.
- Stream returned by `Files.list` should be closed, which is why try-with-resources is used.


```text
`Path represents a file or directory location, and 
`Files` performs operations on it.
File operations can fail, so they must handle `IOException`.
Resources such as readers and streams should be closed automatically with try-with-resources.
```