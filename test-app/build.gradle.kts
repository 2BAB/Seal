plugins {
    id("com.android.application")
    kotlin("android")
    id("me.2bab.seal")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")
    defaultConfig {
        applicationId = "me.xx2bab.seal.sample"
        minSdkVersion(23)
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
    implementation(project(":test-library"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlinVersion"].toString()}")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
}

/**
 * Except the tag removing, any other delete/update features should always consider the
 * "tools:replace", "tools:remove", and other official features that manifest merger provided
 * as higher priority.
 *
 * Functionality that Seal provided is more like a silver bullet to save an urgent publish that
 * is blocked by ManifestMerger. Developers should take responsibility to report bugs to
 * library authors(who introduced problematic Manifest), ManifestMerger(Google), AAPT2(Google),
 * which is the true way to solve the merge issues.
 */
seal {

    // 0. Two cases for before merge.
    beforeMerge("Remove description attr for library input Manifest.")
        .tag("application")
        .attr("android:description")
        .deleteAttr()
    beforeMerge("Remove problematic replace attr for library input Manifest.")
        .tag("application")
        .attr("tools:replace")
        .deleteAttr()

    // Full covered cases for after merge (1-5).
    // 1. THIS IS DANGEROUS, please specify the attr and value if possible.
    afterMerge("Remove all uses-feature tags.")
        .tag("uses-feature")
        .deleteTag()

    // 2. THIS IS DANGEROUS, please specify the value if possible.
    afterMerge("Remove all custom permission tags.")
        .tag("permission")
        .attr("android:protectionLevel")
        .deleteTag()

    // 3. This is the way we recommend to delete the tag(s).
    afterMerge("Remove invalid service tag.")
        .tag("service")
        .attr("android:name")
        .value("me.xx2bab.seal.sample.library.LegacyService")
        .deleteTag()

    // You should try to use "tools:remove" or "tools:replace" instead of "deleteAttr" if possible
    // 4. To delete an attr and its value.
    afterMerge("Remove application's allowBackup attr.")
        .tag("application")
        .attr("android:allowBackup")
        .deleteAttr()

    // You should try to use "tools:remove" or "tools:replace" instead of "deleteAttr" if possible
    // 5. Also u can specify the value as part of finding params.
//    afterMerge("Remove application's allowBackup attr.")
//        .tag("application")
//        .attr("android:allowBackup")
//        .value("true")
//        .deleteAttr()

}
