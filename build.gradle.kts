buildscript {

    val props = java.util.Properties()
    file("./seal/buildSrc/src/main/resources/versions.properties").inputStream().use { props.load(it) }

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = props["kotlinVersion"]?.toString()))
        classpath("com.android.tools.build:gradle:${props["agpVersion"]?.toString()}")
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