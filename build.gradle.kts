buildscript {

    project.extra["kotlinVersion"] = "1.4.32"
    project.extra["agpVersion"] = "4.2.0-rc01"

    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = project.extra["kotlinVersion"].toString()))
        classpath("com.android.tools.build:gradle:${project.extra["agpVersion"]}")
        classpath("me.2bab:seal:+")
    }

}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}