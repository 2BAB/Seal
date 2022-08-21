plugins {
    `kotlin-dsl`
}

dependencies {
    // Github Release
    implementation("com.github.breadmoirai:github-release:2.2.12")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}