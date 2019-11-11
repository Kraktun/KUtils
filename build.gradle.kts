import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    maven
}

group = "com.github.kraktun"
version = "0.0.1"
val coroutinesVersion = "1.3.2"

repositories {
    mavenCentral()
	jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
	compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = project.name
    manifest {
        attributes["Implementation-Title"] = "KUtils"
        attributes["Implementation-Version"] = version
        // attributes["Main-Class"] = "com.kraktun.kutils.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
    kotlinOptions.jvmTarget = "1.8"
}