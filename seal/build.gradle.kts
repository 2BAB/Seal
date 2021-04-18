buildscript {
    val props = java.util.Properties()
    file("./buildSrc/src/main/resources/versions.properties").inputStream().use { props.load(it) }

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = props["kotlinVersion"]?.toString()))
    }
}

allprojects {
    group = "me.2bab"
    version = "3.0.2-rc1"
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}
