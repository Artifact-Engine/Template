plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.openartifact.test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.kotlin-graphics:glm:0.9.9.1-12")

    implementation(project(":Artifact"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}