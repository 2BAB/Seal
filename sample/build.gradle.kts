buildscript {

    project.extra["kotlinVersion"] = "1.4.10"
    project.extra["agpVersion"] = "4.2.0-alpha15"

    repositories {
        google()
        jcenter()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = project.extra["kotlinVersion"].toString()))
        classpath("com.android.tools.build:gradle:${project.extra["agpVersion"]}")
        // Switch to test local / remote dependencies
        classpath(fileTree(mapOf("dir" to "../build/libs", "include" to arrayOf("*.jar"))))
        classpath("me.2bab:polyfill:0.1.3")
//        classpath("me.2bab:seal:3.0.0")
    }

}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}