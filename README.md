# MCServerUpdater

> This is a simple program that will check for updates to Minecraft server jar and download them if they are available.

## Download

Download the latest version of MCServerUpdater
from [here](https://github.com/HSGamer/MCServerUpdater/releases)

## Arguments

```
Option                           Description
------                           -----------
--build <String>                 The build of the project to download (default: latest)
--help                           Get the list of arguments
--output <String>                The output file path (default: server.jar)
--project <String>               The project to download (default: paper)
--projects                       Get the list of projects
--version <String>               The project version (default: default)
```

## Examples

* List all available projects
    * `java -jar MCServerUpdater.jar --projects`
* Download the latest build of Paper
    * `java -jar MCServerUpdater.jar`
    * `java -jar MCServerUpdater.jar --project paper`
* Download the latest build of Paper and save it to the file `server.jar`
    * `java -jar MCServerUpdater.jar --output server.jar`
    * `java -jar MCServerUpdater.jar --project paper --output server.jar`
* Download the latest build of Airplane 1.17.1
    * `java -jar MCServerUpdater.jar --project airplane --version 1.17.1`
    * `java -jar MCServerUpdater.jar --project airplane --version 1.17.1 --build latest`
* Download the #106 build of Airplane 1.17.1
    * `java -jar MCServerUpdater.jar --project airplane --version 1.17.1 --build 106`

## Use this in your projects

1. Add this as a dependency

* Maven

```xml

<repositories>
    <repository>
        <id>codemc-repo</id>
        <url>https://repo.codemc.io/repository/maven-public/</url>
    </repository>
</repositories>
```

```xml

<dependencies>
    <dependency>
        <groupId>me.hsgamer</groupId>
        <artifactId>mc-server-updater-lib</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

* Gradle

```groovy
repositories {
    maven { url = "https://repo.codemc.io/repository/maven-public/" }
}

dependencies {
    compileOnly(group: 'me.hsgamer', name: 'mc-server-updater-lib', version: 'VERSION')
}
```

2. Use the `UpdateBuilder` to create, execute the update process and get the result as `UpdateStatus`

```java
import java.util.concurrent.CompletableFuture;

class Main {
  public static void main(String[] args) {
    UpdateBuilder builder = UpdateBuilder.updateProject("paper")
            .version("1.17.1")
            .build("latest")
            .outputFile("server.jar");

    // Execute the update process asynchronously
    CompletableFuture<UpdateStatus> future = builder.executeAsync();
    
    // Execute the update process synchronously
    UpdateStatus status = builder.execute();
  }
}
```

3. Do whatever you want with the `UpdateStatus`

## Additional System Properties

| Name                             | Description                                                          | Default |
|----------------------------------|----------------------------------------------------------------------|---------|
| `MCServerUpdater.javaExecutable` | The Java executable to run external processes (Spigot Updater, etc.) | `java`  |