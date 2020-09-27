import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import org.apache.commons.lang.StringUtils
import java.util.*

val libs = File(project.buildDir.absoluteFile, "libs")
val taskName = "releaseArtifactsToGithub"
val artifacts: DirectoryProperty = project.objects.directoryProperty()
artifacts.set(libs)

// Temporary workaround for directory is not recognized by ReleaseAssets
gradle.taskGraph.whenReady {
    beforeTask {
        if (this is GithubReleaseTask) {
            this.setReleaseAssets(libs.listFiles())
        }
    }
}

val token: String = if (StringUtils.isNotBlank(System.getenv("GH_DEV_TOKEN"))) {
    System.getenv("GH_DEV_TOKEN")
} else {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    properties.getProperty("github.devtoken")
}

val repo = "seal"
val tagBranch = "master"
val version = project.version.toString()
val releaseNotes = ""
createGithubReleaseTaskInternal(artifacts, token, repo, tagBranch, version, releaseNotes)


fun createGithubReleaseTaskInternal(artifacts: DirectoryProperty,
                                    token: String,
                                    repo: String,
                                    tagBranch: String,
                                    version: String,
                                    releaseNotes: String): TaskProvider<GithubReleaseTask> {
//    val id = version.replace(".", "")
    return project.tasks.register<GithubReleaseTask>("releaseArtifactsToGithub") {
        setAuthorization("Token $token")
        setOwner("2bab")
        setRepo(repo)
        setTagName(version)
        setTargetCommitish(tagBranch)
        setReleaseName("v${version}")
        setBody(releaseNotes)
        setDraft(false)
        setPrerelease(false)
        setReleaseAssets(artifacts)
        setOverwrite(true)
        setAllowUploadToExisting(true)
        setApiEndpoint("https://api.github.com")
        setDryRun(false)
    }
}

