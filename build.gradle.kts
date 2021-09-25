import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.kraktun"
version = "0.0.6"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

val coroutinesVersion = "1.5.2-native-mt"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.30")
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
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

artifacts {
    archives(sourcesJar)
}

java {
    //withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "KUtils"
            from(components["java"])
            pom {
                name.set("KUtils")
                description.set("Collection of utilities for Kotlin and Java")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://spdx.org/licenses/MIT.html")
                    }
                }
            }
        }
    }
}