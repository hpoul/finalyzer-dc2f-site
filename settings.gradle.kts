/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/5.2/userguide/multi_project_builds.html
 */

rootProject.name = "finalyzer-dc2f-site"

//include("dc2f")
//project(":dc2f").projectDir = file("deps/dc2f.kt")

includeBuild("./deps/dc2f.kt")
