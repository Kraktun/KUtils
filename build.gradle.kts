import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.8.10"
    id("org.jmailen.kotlinter") version "3.14.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.kraktun"
version = "0.0.8"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

val coroutinesVersion = "1.6.4"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<KotlinCompile>().all {
    //kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
    kotlinOptions.jvmTarget = "11"
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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