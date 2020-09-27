plugins {
    `kotlin-dsl`
}

dependencies {
    implementation ("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
    // Github Release
    implementation("com.github.breadmoirai:github-release:2.2.12")
}

repositories {
    jcenter()
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }
}