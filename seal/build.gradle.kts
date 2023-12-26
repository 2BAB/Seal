plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization")
    id("java-gradle-plugin")
    `github-release`
    `maven-central-publish`
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
    plugins {
        create("seal") {
            id = "me.2bab.seal"
            implementationClass ="me.xx2bab.seal.SealPlugin"
        }
    }
}

dependencies {
    implementation(deps.polyfill.main)

    implementation(gradleApi())
    implementation(deps.kotlin.std)
    implementation(deps.kotlin.serialization)

    compileOnly(deps.android.gradle.plugin)

    testImplementation(gradleTestKit())
    testImplementation(deps.zip4j)
}
