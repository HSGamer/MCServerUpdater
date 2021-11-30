# MCServerUpdater
> This is a simple program that will check for updates to Minecraft server jar and download them if they are available.

## Download
Download the latest version of MCServerUpdater from [here](https://github.com/HSGamer/MCServerUpdater/releases/tag/1.0.1)

## Arguments
```
Option                           Description
------                           -----------
--build <String>                 The build of the project to download (default: latest)
--help                           Get the list of arguments
--output <String>                The output file path (default: server.jar)
--project <String>               The project to download (default: paper)
--projects                       Get the list of projects
--skip-internet-check [Boolean]  Skip the internet check (default: false)
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