# Artifact Test Project

A test project for the [Artifact Game Engine](https://www.github.com/meo209/Artifact).

Setting it up requires Java 8+, Kotlin and the Artifact Engine to be downloaded.

The folder structure should look like this:

```
IdeaProjects
 - Artifact
  - src
  - gradle
 - ArtifactTestProject
  - src
  - gradle
```

The [settings.gradle.kts]() must contain:
```kotlin
includeFlat("Artifact")
```

The [build.gradle.kts]() has to include:
```kotlin
implementation(project(":ArtifactDebug"))
```

Running the project requires creating a run configuration like this:

![runConfiguration in intellij](docs/runConfig.png "Run Configuration in IntelliJ idea")

Make sure to add the "copyResourcesToProjectGameData" and "copyResourcesToEngineData" gradle task **before** the build task.

If using the debug level, add the following to the VM options:
```
-Dorg.slf4j.simpleLogger.defaultLogLevel=debug 
```

## Note
You may want to use [ArtifactDebug](https://www.github.com/meo209/ArtifactDebug) for easier development.

### Using ArtifactDebug
To use ArtifactDebug you need to download it and place the folder in the same parent directory as your project.
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
