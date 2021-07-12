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

Additionally, if you want to request multiple objects at the same time without regenerating the project,
you can queue up requests to be completed the next time the project is generated.
```java
// These futures will spin until the next time the instance is generated
CompletableFuture<CodeModel> codeModelFuture = instance.queueRequest(CFIQuery.CODE_MODEL);
CompletableFuture<Cache> cacheFuture = instance.queueRequest(CFIQuery.CACHE);
CompletableFuture<CMakeFiles> cMakeFilesFuture = instance.queueRequest(CFIQuery.CMAKE_FILES);
CompletableFuture<Toolchains> toolchainsFuture = instance.queueRequest(CFIQuery.TOOLCHAINS);

instance.generate();

// These futures will now complete when the generation is finished
CodeModel codeModel = codeModelFuture.join();
Cache cache = cacheFuture.join();
CMakeFiles cMakeFiles = cMakeFilesFuture.join();
Toolchains toolchains = toolchainsFuture.join();
```

Various sub-objects will have a `jsonFile` attribute that can be read using `instance#readReplyObject`.
For instance, `CodeModel.Configuration.Target` can be read as the `Target` sub-object.
```java
CodeModel codeModel = ...;
CodeModel.Configuration.Target targetReference = codeModel.configurations.get(0).targets.get(0);
Target target = instance.readReplyObject(Target.class, targetReference.jsonFile);
```