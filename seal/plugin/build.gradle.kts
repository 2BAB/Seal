import BuildConfig.Deps

plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    `github-release`
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
    implementation(gradleApi())
    implementation(kotlin(Deps.ktStd))
    compileOnly(Deps.agp)
    implementation(Deps.polyfill)

    testImplementation(gradleTestKit())
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
    testImplementation(Deps.fastJson)
    testImplementation(Deps.zip4j)
}