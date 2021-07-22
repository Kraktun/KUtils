import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    `java-library`
    java
    kotlin("jvm") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.github.kraktun"
version = "0.0.4"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

val coroutinesVersion = "1.5.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
    kotlinOptions.jvmTarget = "1.8"
}

val sourcesJar = task("sourcesJar", type = Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE
}

artifacts {
    archives(sourcesJar)
}