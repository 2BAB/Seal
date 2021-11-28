plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("java-gradle-plugin")
    `github-release`
    `maven-central-publish`
    `functional-test-setup`
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    plugins {
        create("seal") {
            id = "seal"
            implementationClass ="me.xx2bab.seal.SealPlugin"
        }
    }
}

dependencies {
    implementation(deps.polyfill.main)
    implementation(deps.polyfill.manifest)

    implementation(gradleApi())
    implementation(deps.kotlin.std)
    implementation(deps.kotlin.serialization)

    compileOnly(deps.android.gradle.plugin)

    testImplementation(gradleTestKit())
    testImplementation(deps.junit)
    testImplementation(deps.mockito)
    testImplementation(deps.mockitoInline)
    testImplementation(deps.fastJson)
    testImplementation(deps.zip4j)
}