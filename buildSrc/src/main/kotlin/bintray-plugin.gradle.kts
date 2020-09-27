import java.util.Properties
import org.apache.commons.lang.StringUtils

plugins{
    `maven-publish`
    id("com.jfrog.bintray")
}

val mavenDesc = "A Gradle Plugin to resolve AndroidManifest.xml merge conflicts."
val baseUrl = "https://github.com/2BAB/Seal"
val siteUrl = baseUrl
val gitUrl = "${baseUrl}.git"
val issueUrl = "${baseUrl}/issues"

val licenseIds = "Apache-2.0"
val licenseNames = arrayOf("The Apache Software License, Version 2.0")
val licenseUrls = arrayOf("http://www.apache.org/licenses/LICENSE-2.0.txt")
val inception = "2017"

val username = "2BAB"
val projectId = "seal"

publishing {
    publications {
        create<MavenPublication>("SealPlugin") {
            from(components["java"])
            pom {
                // Description
                name.set(projectId)
                description.set(mavenDesc)
                url.set(siteUrl)

                // Archive
                groupId = project.group.toString()
                artifactId = projectId
                version = project.version.toString()

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

    repositories {
        maven {
            name = "myMavenlocal"
            url = uri(System.getProperty("user.home") + "/.m2/repository")
        }
    }
}

var btUser: String?
var btApiKey: String?

if (StringUtils.isNotBlank(System.getenv("BINTRAY_USER"))) {
    btUser = System.getenv("BINTRAY_USER")
    btApiKey = System.getenv("BINTRAY_APIKEY")
} else {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    btUser = properties.getProperty("bintray.user")
    btApiKey = properties.getProperty("bintray.apikey")
}

bintray{
    user = btUser
    key = btApiKey
    setPublications("SealPlugin")
    pkg.apply {
        repo = "maven"
        name = projectId
        desc = mavenDesc
        websiteUrl = siteUrl
        issueTrackerUrl = issueUrl
        vcsUrl = gitUrl
        setLabels("2BAB", "Gradle", "Seal", "AndroidManifest", "Conflicts", "Merge", "Fix", "Replace", "Remove")
        setLicenses(licenseIds)
        publish = true
        publicDownloadNumbers = true
    }
}