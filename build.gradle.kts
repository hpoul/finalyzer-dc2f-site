import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm").version("1.3.20")

    // Apply the application plugin to add support for building a CLI application.
    application
}

sourceSets {
    main {
        resources {
            srcDir("web/content")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//    implementation(project("dc2f"))
    implementation("com.dc2f:dc2f:0.0.1-SNAPSHOT")

//    api("com.fasterxml.jackson.core:jackson-annotations")
    implementation("io.github.microutils:kotlin-logging:1.4.9")

    // utils
    implementation("org.apache.commons:commons-text:1.6")
    implementation("org.apache.commons:commons-lang3:3.8.1")
    implementation("commons-io:commons-io:2.6")



    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    // Define the main class for the application.
    mainClassName = "app.anlage.site.AppKt"
}
