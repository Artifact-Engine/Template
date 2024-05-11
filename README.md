# Artifact Test Project

A test project for the [Artifact Game Engine](https://www.github.com/Artifact-Engine/Artifact).

### Note
The setup process will change in the future.

## Setup
### Requirements:
Java (JVM & JDK) <br>
Kotlin <br>
IntelliJ (Or any other IDE that supports kotlin & java) <br>
Artifact Engine

### Structure

The folder structure should look like this:

```
<root folder> (e.g. IdeaProjects)
 - Artifact
  - src
  - gradle
 - ArtifactTestProject
  - src
  - gradle
```

At startup Artifact will search for all classes that have the annotation 
```kotlin
@Entry
```
present.

If a class is found it will load it, and create an Application.
The application will then be passed to the Engine for further processing.

Here is an example GameClass:
```kotlin
@Entry
class GameClass : Application() {
    
    override fun init() {
        logger.info("Game init")
    }

    override fun update() {
        logger.info("Game update")
    }

    override fun shutdown() {
        logger.info("Game Shutdown")
    }
}
```

Make **sure** to inherit the ```Application``` class in your GameClass.

### Start

Running the project requires creating a run configuration like this:

![runConfiguration in intellij](docs/runConfig.png "Run Configuration in IntelliJ idea")

Make **sure** to set "Use classpath of module" to ```ArtifactTestProject.main```.

To get debug logs, add the following line to your VM options:
```
-Dorg.slf4j.simpleLogger.defaultLogLevel=debug 
```

## Building
Building is not yet supported. You will need to rely on your IDE for the moment.

## Note
You may want to use [ArtifactDebug](https://www.github.com/Artifact-Engine/ArtifactDebug) for easier development.

### Using ArtifactDebug
To use ArtifactDebug you need to download it and place the downloaded directory in the same parent directory as your project (e.g. IdeaProjects).

Then add this line to your [settings.gradle.kts]():
```kotlin
includeFlat("ArtifactDebug")
```
Then, add the following to your [build.gradle.kts]():
```kotlin
implementation(project(":ArtifactDebug"))
```

# License
[GPLv3](https://www.gnu.org/licenses/gpl-3.0.html)
