import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.compose

group = "sunnyxibei"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

plugins {
    kotlin("jvm") version "1.4.0"
    id("org.jetbrains.compose") version ("0.1.0-build113")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(kotlin("stdlib-jdk8"))
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
