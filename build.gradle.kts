import BuildConfig.Deps

buildscript {
    group = "me.2bab"
    version = "3.0.0-beta01"

    repositories {
        google()
        jcenter()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    kotlin("jvm") version "1.4.10"
//    idea
//    `java-gradle-plugin`
    `bintray-plugin`
    `github-release`
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jre7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jre8")
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin(Deps.ktStd))
    implementation(Deps.agp)
    implementation(Deps.polyfill)

    testImplementation(gradleTestKit())
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
    testImplementation(Deps.fastJson)
    testImplementation(Deps.zip4j)
}

tasks.compileJava {
    options.compilerArgs.plusAssign(arrayOf("-proc:none"))
}

// >>> The functest configuration is not compatible with current project structure,
// >>> it may caused by
//
//val funcTestSourceSet: SourceSet = sourceSets.create("funcTest") {
//    compileClasspath += sourceSets.main.get().output
//    runtimeClasspath += sourceSets.main.get().output
//}
//
//val funcTestImplementation: Configuration by configurations.getting {
//    extendsFrom(configurations.testImplementation.get())
//}
//
//gradlePlugin.testSourceSets(funcTestSourceSet)
//
//idea {
//    module {
//        testSourceDirs = testSourceDirs.plus(funcTestSourceSet.allSource.srcDirs)
//        testResourceDirs = testResourceDirs.plus(funcTestSourceSet.resources.srcDirs)
//
//        val plusCollection = scopes["TEST"]?.get("plus")
//        plusCollection?.addAll(funcTestImplementation.all.filter {
//            it.name.contains("funcTestCompileClasspath")
//                    || it.name.contains("funcTestRuntimeClasspath")
//        })
//    }
//}
//
//val functionTest by tasks.registering(Test::class) {
//    dependsOn(project.parent!!.tasks.getByPath("assemble"))
//    description = "Runs function tests."
//    group = "verification"
//    testClassesDirs = funcTestSourceSet.output.classesDirs
//    classpath = funcTestSourceSet.runtimeClasspath
//}
//
//val check by tasks.getting(Task::class) {
//    dependsOn(functionTest)
//}

tasks.getByPath(":test").dependsOn(":assemble")