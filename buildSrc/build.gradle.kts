plugins {
    `kotlin-dsl`
}

dependencies {
    // Github Release
    implementation("com.github.breadmoirai:github-release:2.5.2")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}