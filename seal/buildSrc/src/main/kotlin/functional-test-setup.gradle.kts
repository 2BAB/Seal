import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.*

plugins {
    `java-gradle-plugin`
    idea
}

val defaultAGPVer = BuildConfig.props["agpVersion"].toString()
val defaultAGP = BuildConfig.Deps.agp

val fixtureClasspath: Configuration by configurations.creating
tasks.pluginUnderTestMetadata {
    pluginClasspath.from(fixtureClasspath)
}

val functionalTestSourceSet: SourceSet = sourceSets.create("functionalTest") {
    compileClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath.get()
    runtimeClasspath += output + compileClasspath
}

val functionalTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

idea {
    module {
        testSourceDirs = testSourceDirs.plus(functionalTestSourceSet.allSource.srcDirs)
        testResourceDirs = testResourceDirs.plus(functionalTestSourceSet.resources.srcDirs)

        val plusCollection = scopes["TEST"]?.get("plus")
        plusCollection?.addAll(functionalTestImplementation.all.filter {
            it.name.contains("functionalTestCompileClasspath")
                    || it.name.contains("functionalTestRuntimeClasspath")
        })
    }
}

val functionalTest by tasks.registering(Test::class) {
    failFast = true
    description = "Runs functional tests."
    group = "verification"
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

val check by tasks.getting(Task::class) {
    dependsOn(functionalTest)
}

val test by tasks.getting(Test::class) {
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

@Suppress("UnstableApiUsage")
val fixtureAgpVersion: String = providers
    .environmentVariable("AGP_VERSION")
    .forUseAtConfigurationTime()
    .orElse(providers.gradleProperty("agpVersion").forUseAtConfigurationTime())
    .getOrElse(defaultAGPVer)


dependencies {
    compileOnly(defaultAGP) // Let the test resource or user decide

    functionalTestImplementation("com.android.tools.build:gradle:${fixtureAgpVersion}")
    fixtureClasspath("com.android.tools.build:gradle:${fixtureAgpVersion}")
}
