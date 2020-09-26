plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")

    id("me.2bab.seal")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "me.xx2bab.seal.sample"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":library"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlinVersion"].toString()}")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
}

seal {
    beforeMerge("Remove description attr.")
        .tag("application")
        .attr("android:description")
        .deleteAttr()

    beforeMerge("Remove invalid service tag.")
        .tag("service")
        .attr("android:name")
        .value("me.xx2bab.seal.sample.library.LegacyService")
        .deleteTag()

    afterMerge("Remove application's allowBackup.")
        .tag("application")
        .attr("android:allowBackup")
        .value("true")
        .deleteAttr()
}