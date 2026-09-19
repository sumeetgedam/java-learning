# Lesson 1: Java Execution

## Questions

1. What is the difference between JDK, JRE, and JVM?
2. What does `javac` do?
3. What does the `java` command do?
4. Why can the same `.class` file run on different operating systems?
5. What is bytecode?
6. What is the difference between the Java version and the JVM version?

## My summary

- JDK : tools for developing Java applications
  - Includes javac, java, debugger, and other tools
- JRE : runtime environment for executing Java Applications
  - Conceptually includes the JVM and runtime libraries
- JVM : executes Java bytecode
  - JVM : platform-specific
  - Java Bytecode : platform independent
- javac complies the source code and creates .class files
- java executes the compiled classes

```text
Java source code is complied by the JDK into bytecode, 
and the JVM executes that bytecode using the runtime
libraries available in Java environment
```
## Commands practiced

```text
java -version
javac -version
mvn -version
javac
java
javap
```


