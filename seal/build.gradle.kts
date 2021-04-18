import BuildConfig.Deps

buildscript {
    group = "me.2bab"
    version = "3.0.2-rc1"

    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}
