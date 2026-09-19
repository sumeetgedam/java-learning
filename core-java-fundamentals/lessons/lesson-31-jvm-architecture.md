# Lesson 31: JVM Architecture

## Questions

1. What is stored in the heap?
2. What is stored in a thread's stack?
3. What is a stack frame?
4. Why can recursion cause StackOverflowError?
5. What is the method area?
6. What is metaspace?
7. What does a ClassLoader do?
8. What is the class-loader delegation model?
9. What does the JIT compiler do?
10. What makes an objet eligible for garbage collection?
11. What is the difference between heap and non-heap memory?
12. Why is System.gc() not a reliable way to force garbage collection?

## My summary

Java Source
    | javac
    v
Bytecode (.class)
    | ClassLoader
    v
JVM
    |-- Heap
    |-- Thread stacks
    |-- Method area / class metadata
    |-- Program counter
    |-- Native method stack
    |-- Interpreter
    |-- JIT compiler

- Heap stores :
  - Objects
  - Arrays
  - Instance fields
- The heap is shared by all threads
  - Objects that are no longer reachable can eventually be reclaimed by the garbage collectors.
- Each thread has it own `JVM Stack`.
  - A stack contains method frames, local variables, intermediate values, and method call information
  - Stack characteristics
    - Private to each thread
    - Stores method frames
    - Contains local primitive values
    - Contains object references
    - Can cause StackOverflowError
      - Deep or infinite recursion can exhaust a thread's stack
 
| Feature      | Stack                      | Heap                  |
|--------------|----------------------------|-----------------------|
| Ownership    | Per thread                 | shared                |
| Stores       | Frames, locals, references | Objects and arrays    |
| Lifetime     | Method invocation          | Object reachability   |
| Managed By   | Method return              | Garbage collector     |
| Common error | StackOverflowError         | OutOfMemoryError      |

- Avoid saying simply "all primitives are on the stack".
  - A primitive field inside an object is part of that heap object, and JVM implementations may optimize allocations.
- The JVM specification defines a shared method area for per-class structures such as : 
  - Runtime constant pool
  - Field information
  - Method information
  - Method byteCode
- In Hotspot, class metadata is commonly associated with Metaspace.
  - Class metadata
    - Class name
    - Method metadata
    - Field metadata
    - Runtime constant pool
    - Class-loader information
  - Metaspace is not the same thing as Java Heap
- A `ClassLoader` loads a class definitions into JVM.
  - A class loader generally follows a delegation model
    - It asks its parent loader first before attempting to load a class itself
  - Bootstrap ClassLoader
  - Platform ClassLoader
  - Application ClassLoader
- Initially, bytecode can be interpreted by the JVM.
  - Frequently executed code may be compiled into native machine code by the Just-In-Time compiler :
    - ByteCode
    - Interpreter ( detects frequently used code )
    - JIT compiler
    - Native machine code
- The JIT may optimize code using techniques such as : 
  - Method inlining
  - Dead-code elimination
  - Escape analysis
  - Loop optimizations
- Garbage collections
  - Java automatically manages heap memory
  - if no reachable reference points to object, it becomes eligible for garbage collection.
    - Eligible for collection does not mean immediately collected
    - Calling `System.gc()` is only a request
    - Garbage collection is not a substitute for closing files, sockets, or database connections
- java -Xms128m -Xmx256m -cp "core-java-fundamentals\target\classes" com.learning.core.jvm.JvmRuntimeDemo


```text
The heap is shared and stores objects, each thread has its own stack containing method frames,
class loaders load class definitions, and the JIT compiles frequently executed bytecode into
optimized native code. the exact memory layout depends on the JVM implementation
```