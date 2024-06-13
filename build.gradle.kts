import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"

    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val lwjglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else if (arch.startsWith("ppc"))
                "natives-linux-ppc64le"
            else if (arch.startsWith("riscv"))
                "natives-linux-riscv64"
            else
                "natives-linux"

        arrayOf("Windows").any { name.startsWith(it) } ->
            "natives-windows"

        else ->
            throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}

group = "org.openartifact"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.kotlin-graphics:glm:0.9.9.1-12")
    implementation("org.apache.commons:commons-collections4:4.5.0-M1")

    // LWJGL - OpenGL
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.3"))

    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")

    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    implementation(project(":Artifact"))
    implementation(project(":ArtifactDebug"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("${project.name}-$version" +
            ".jar")

    manifest {
        attributes["Main-Class"] = "org.openartifact.artifact.EntryPointKt"
    }
}

tasks.create("export") {
    dependsOn("shadowJar")
}