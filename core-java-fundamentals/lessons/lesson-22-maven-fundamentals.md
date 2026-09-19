# Lesson 22: Maven Fundamentals

## Questions

1. What is Maven?
2. What is the purpose of pom.xml?
3. What are Maven coordinates?
4. What is the purpose of groupId?
5. What is the purpose of artifactId?
6. What is the difference between compile and test dependency scope?
7. What does mvn compile do?
8. What does mvn package do?
9. What does mvn test do?
10. What does mvn clean do?
11. What is the target directory?
12. What is a multi-module Maven project?
13. What is the difference between a lifecycle and a goal?

## My summary

- Maven builds Java projects using project configuration in `pom.xml` and its build lifecycle.
  - Compile Java code
  - Run tests
  - Resolve dependencies
  - Package applications as JARs
  - Install artifacts locally
  - Build multiple modules.
- `pom.xml` : Project Object Model
  - Project Identity
  - Java version
  - Dependencies
  - Plugins
  - Packaging
  - Modules
  - Build configuration
- A Maven artifact is identified by:
  - groupId:artifactId:version
  - example : com.learning:core-java-fundamentals:1.0-SNAPSHOT

| Element | Meaning | 
| --- | --- | 
| groupId | Organization or Project namespace | 
| artifactId | Project/module name | 
| version | Project version | 

- Maven Structure :
  - src/main/java - production Java code
  - src/main/resources - production configuration / resources
  - src/test/java - test code
  - src/test/resources - test resources
  - target - generated build output
- LifeCycle :
  - validate
  - compile
  - test
  - package
  - verify
  - install
  - deploy

| Phase    | Purpose                                 | 
|----------|-----------------------------------------| 
| validate | Validate project configuration          | 
| compile  | Compile main source code                | 
| test     | Compile and run tests                   | 
| package  | Create a JAR or other artifact          | 
| verify   | Run additional quality checks           | 
| install  | Put artifact in local Maven repository  | 
| deploy   | Publish artifact to a remote repository | 

- Maven supports scopes :
  - compile : application dependency
  - test : test only dependency
  - runtime : needed while running, not compiling
  - provided : expected from the runtime environment

```text
Maven reads `pom.xml`, resolves dependencies, and executes build phases.
That standard layout separates production code, test code, resources, and generated output.
A multi-module root POM coordinates the build of several projects.
```

