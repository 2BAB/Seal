rootProject.name = "seal-plugin"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    val versions = file("../deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion = { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        kotlin("jvm") version getVersion("kotlinVer") apply false
        kotlin("plugin.serialization") version getVersion("kotlinVer") apply false
    }
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("deps") {
            from(files("../deps.versions.toml"))
        }
    }
}


include(":plugin")

// To test Polyfill dependency locally, you can uncomment the block below
// and change the path to the Polyfill project path.
// @link https://github.com/2BAB/Polyfill
//includeBuild("../../Polyfill") {
//    dependencySubstitution {
//        substitute(module("me.2bab:polyfill"))
//            .using(project(":polyfill"))
//        substitute(module("me.2bab:polyfill-manifest"))
//            .using(project(":polyfill-manifest"))
//    }
//}