// must keep the order among `buildscript` `plugins` `repositories` blocks
buildscript {
    group = "me.2bab"
    version = "2.0.0.2-SNAPSHOT"

    repositories {
        google()
        jcenter()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    groovy
    kotlin("jvm") version "1.4.10"
    `bintray-plugin`
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        mavenLocal()
    }
}

java {
    withSourcesJar()
}

configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jre7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jre8")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.android.tools.build:gradle:4.2.0-alpha12")

    implementation("me.2bab:polyfill:0.1.2")
}

tasks.compileJava {
    options.compilerArgs.plusAssign(arrayOf("-proc:none"))
}

