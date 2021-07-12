# cfi-java
A Java library to interact with a CMake project through the CMake File Api.

## Download / Installation

### Gradle
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "me.walkerknapp:cfi-java:0.0.1"
}
```

### Maven
```xml
<dependency>
    <groupId>me.walkerknapp</groupId>
    <artifactId>cfi-java</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Usage

Using `cfi-java` starts with `CMakeProject` and `CMakeInstancce` instances.

`CMakeProject` takes a path to the project built with CMake.
`CMakeInstance` takes a path to the directory to build the project in.
This can be the same as the path to the project itself.

```java
CMakeProject project = new CMakeProject(Paths.get("path/to/cmake/project/sources"))
CMakeInstance instance = new CMakeInstance(project, Paths.get("path/to/cmake/build/directory"))
```

With a `CMakeInstance`, you can request top-level objects with:
```java
CompletableFuture<CodeModel> codeModel = instance.requestObject(CFIQuery.CODE_MODEL);
CompletableFuture<Cache> cache = instance.requestObject(CFIQuery.CACHE);
CompletableFuture<CMakeFiles> cMakeFiles = instance.requestObject(CFIQuery.CMAKE_FILES);
CompletableFuture<Toolchains> toolchains = instance.requestObject(CFIQuery.TOOLCHAINS);
```

Various sub-objects will have a `jsonFile` attribute that can be read using `instance#readReplyObject`.
For instance, `CodeModel.Configuration.Target` can be read as the `Target` sub-object.
```java
CodeModel codeModel = ...;
CodeModel.Configuration.Target targetReference = codeModel.configurations.get(0).targets.get(0);
Target target = instance.readReplyObject(Target.class, targetReference.jsonFile);
```