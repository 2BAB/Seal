plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "me.xx2bab.seal.sample.library"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
//        isAbortOnError = false
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(deps.kotlin.std)
}