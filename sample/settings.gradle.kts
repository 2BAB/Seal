rootProject.name = "seal-root"

pluginManagement {
    extra["externalDependencyBaseDir"] = "../"
    val versions = file(extra["externalDependencyBaseDir"].toString() + "deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion = { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        kotlin("android") version getVersion("kotlinVer") apply false
        id("com.android.application") version getVersion("agpVer") apply false
        id("com.android.library") version getVersion("agpVer") apply false
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "me.2bab.seal") {
                // It will be replaced by a local module using `includeBuild` below,
                // thus we just put a generic version (+) here.
                useModule("me.2bab:seal:+")
            }
        }
    }
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        mavenLocal()
    }
}

val externalDependencyBaseDir = extra["externalDependencyBaseDir"].toString()
val enabledCompositionBuild = true

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("deps") {
            from(files(externalDependencyBaseDir + "deps.versions.toml"))
        }
    }
}

include(":test-app", ":test-library")
if (enabledCompositionBuild) {
    includeBuild(externalDependencyBaseDir) {
        dependencySubstitution {
            substitute(module("me.2bab:seal"))
                .using(project(":plugin"))
        }
    }
}