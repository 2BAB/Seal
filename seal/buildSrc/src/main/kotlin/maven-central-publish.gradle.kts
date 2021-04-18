plugins{
    `maven-publish`
    signing
}


// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrh.username"] = null
ext["ossrh.password"] = null

// Grabbing secrets from local.properties file or from environment variables,
// which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        java.util.Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrh.username"] = System.getenv("OSSRH_USERNAME")
    ext["ossrh.password"] = System.getenv("OSSRH_PASSWORD")
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}
fun getExtraString(name: String) = ext[name]?.toString()


val groupName = "me.2bab"
val projectName = "seal"
val mavenDesc = "A Gradle Plugin to resolve AndroidManifest.xml merge conflicts."
val baseUrl = "https://github.com/2BAB/Seal"
val siteUrl = baseUrl
val gitUrl = "$baseUrl.git"
val issueUrl = "$baseUrl/issues"

val licenseIds = "Apache-2.0"
val licenseNames = arrayOf("The Apache Software License, Version 2.0")
val licenseUrls = arrayOf("http://www.apache.org/licenses/LICENSE-2.0.txt")
val inception = "2017"

val username = "2BAB"


publishing {

    publications {
        create<MavenPublication>("SealPlugin") {
            artifact(javadocJar.get())
            from(components["java"])
            pom {
                // Description
                name.set(projectName)
                description.set(mavenDesc)
                url.set(siteUrl)

                // Archive
                groupId = groupName
                artifactId = projectName
                version = BuildConfig.Versions.sealVersion

                // License
                inceptionYear.set(inception)
                licenses {
                    licenseNames.forEachIndexed { ln, li ->
                        license {
                            name.set(li)
                            url.set(licenseUrls[ln])
                        }
                    }
                }
                developers {
                    developer {
                        name.set(username)
                    }
                }
                scm {
                    connection.set(gitUrl)
                    developerConnection.set(gitUrl)
                    url.set(siteUrl)
                }
            }
        }
    }

    // Configure MavenCentral repository
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrh.username")
                password = getExtraString("ossrh.password")
            }
        }
    }

    // Configure MavenLocal repository
    repositories {
        maven {
            name = "myMavenlocal"
            url = uri(System.getProperty("user.home") + "/.m2/repository")
        }
    }
}

signing {
    sign(publishing.publications)
}