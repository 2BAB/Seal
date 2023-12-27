import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import java.io.File
import java.util.*

val taskName = "releaseArtifactsToGithub"

val tokenFromEnv: String? = System.getenv("GH_DEV_TOKEN")
val token: String = if (!tokenFromEnv.isNullOrBlank()) {
    tokenFromEnv
} else if (project.rootProject.file("local.properties").exists()) {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    properties.getProperty("github.devtoken")
} else {
    ""
}


val repo = "seal"
val tagBranch = "master"
val version = project.version.toString()
val releaseNotes = ""
val task = createGithubReleaseTaskInternal(token, repo, tagBranch, version, releaseNotes)


fun createGithubReleaseTaskInternal(
    token: String,
    repo: String,
    tagBranch: String,
    version: String,
    releaseNotes: String
): TaskProvider<GithubReleaseTask> {
    return project.tasks.register<GithubReleaseTask>(taskName) {
        authorization.set("Token $token")
        owner.set("2bab")
        this.repo.set(repo)
        tagName.set(version)
        targetCommitish.set(tagBranch)
        releaseName.set("v${version}")
        body.set(releaseNotes)
        draft.set(false)
        prerelease.set(false)
        overwrite.set(true)
        allowUploadToExisting.set(true)
        apiEndpoint.set("https://api.github.com")
        dryRun.set(false)
        generateReleaseNotes.set(false)
        releaseAssets.from(
            tasks.getByName<Jar>("jar").archiveFile, // seal-${version}.jar
            tasks.getByName<Jar>("sourcesJar").archiveFile, // seal-${version}-sources.jar
            tasks.getByName<Jar>("javadocJar").archiveFile, // seal-${version}-javadoc.jar
            //tasks.getByName<Sign>("signPluginMavenPublication").outputs, // seal-${version}-asc.jar, seal-${version}-sources-asc.jar, seal-${version}-sources-asc.jar,
        )
    }
}