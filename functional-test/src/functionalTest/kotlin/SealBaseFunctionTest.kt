import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import me.xx2bab.polyfill.manifest.bytes.parser.ManifestBytesTweaker
import net.lingala.zip4j.ZipFile
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.util.concurrent.TimeUnit

class SealBaseFunctionTest {

    companion object {

        private const val testProjectPath = "../sample"
        private const val testProjectJsonOutputPath = "${testProjectPath}/build/functionTestOutput"
        private const val testProjectAppOutputPath = "./build/sample-%s/test-app/build/outputs/apk/debug"
        private const val testProjectAppUnzipPath = "./build/sample-%s/test-app/build/outputs/apk/debug/unzipped"
        private val tweakerMap = mutableMapOf<String, ManifestBytesTweaker>()

        @BeforeAll
        @JvmStatic
        fun buildTestProject() {
            if (File("../local.properties").exists()) {
                println("Publishing libraries to MavenLocal...")
                ("./gradlew" + " :seal:publishPluginMavenPublicationToMyMavenlocalRepository"
                        + " --stacktrace").runCommand(File("../"))
                ("./gradlew" + " :seal:publishSealPluginMarkerMavenPublicationToMyMavenlocalRepository"
                        + " --stacktrace").runCommand(File("../"))
                println("All libraries published.")
            }
            runBlocking(Dispatchers.IO) {
                agpVerProvider().map { agpVer ->
                    async {
                        println(
                            "Copying project for AGP [${agpVer}] from ${
                                File(testProjectPath).absolutePath
                            }..."
                        )

                        val targetProject = File("./build/sample-$agpVer")
                        targetProject.deleteRecursively()
                        File(testProjectPath).copyRecursively(targetProject)
                        val settings = File(targetProject, "settings.gradle.kts")
                        val newSettings = settings.readText()
                            .replace(
                                "= \"../\"",
                                "= \"../../../\""
                            ) // Redirect the base dir
                            .replace(
                                "enabledCompositionBuild = true",
                                "enabledCompositionBuild = false"
                            ) // Force the app to find plugin from maven local
                            .replace(
                                "getVersion(\"agpVer\")",
                                "\"$agpVer\""
                            ) // Hardcode agp version
                        settings.writeText(newSettings)

                        println("assembleFullDebug for [$agpVer]")

                        GradleRunner.create()
                            .withGradleVersion("8.5")
                            .forwardOutput()
                            .withArguments("clean", "assembleDebug", "--stacktrace", "--scan")
                            .withProjectDir(targetProject)
                            .build()

                        println("Unzipping...")
                        unzipApk(File(testProjectAppOutputPath.format(agpVer)), agpVer)
                        constructManifestTweaker(agpVer)

                        println("Testing...")
                    }
                }.forEach {
                    it.await()
                }
            }
        }

        private fun unzipApk(testProjectAppOutput: File, agpVer: String) {
            testProjectAppOutput
                .walk()
                .filter { it.extension == "apk" }
                .first {
                    val unzipFolder = File(testProjectAppUnzipPath.format(agpVer))
                    if (!unzipFolder.exists()) {
                        unzipFolder.mkdir()
                        ZipFile(it.absolutePath).extractAll(unzipFolder.absolutePath)
                    }
                    true
                }
        }

        private fun constructManifestTweaker(agpVer: String) {
            val extractedAndroidManifest = File(testProjectAppUnzipPath.format(agpVer), "AndroidManifest.xml")
            assertThat(extractedAndroidManifest.exists(), `is`(true))
            val manifestBytesTweaker = ManifestBytesTweaker()
            manifestBytesTweaker.read(extractedAndroidManifest)
            tweakerMap[agpVer] = manifestBytesTweaker
        }

        @JvmStatic
        fun agpVerProvider(): List<String> {
            val versions = File("../deps.versions.toml").readText()
            val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
            val getVersion = { s: String ->
                regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1]
            }
            return listOf(getVersion("agpVer"), getVersion("agpBackportVer"))
        }

        fun String.runCommand(workingDir: File) {
            ProcessBuilder(*split(" ").toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor(15, TimeUnit.MINUTES)
        }

    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun manifestBeforeMerge_deleteAttrsSuccessfully(agpVer: String) {
        val manifestBytesTweaker = tweakerMap[agpVer]!!
        val applicationTag = manifestBytesTweaker.getSpecifyStartTagBodyByName("application")
        assertThat(applicationTag, `is`(notNullValue()))
        val allowBackup = manifestBytesTweaker.getAttrFromTagAttrs(applicationTag!!, "description")
        assertThat(allowBackup, `is`(nullValue()))
        val replace = manifestBytesTweaker.getAttrFromTagAttrs(applicationTag, "replace")
        assertThat(replace, `is`(nullValue()))
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun manifestAfterMerge_deleteTagSuccessfully(agpVer: String) {
        val manifestBytesTweaker = tweakerMap[agpVer]!!
        val feature = manifestBytesTweaker.getSpecifyStartTagBodyByName("uses-feature")
        val permission = manifestBytesTweaker.getSpecifyStartTagBodyByName("permission")
        val service = manifestBytesTweaker.getSpecifyStartTagBodyByName("service")
        assertThat(feature, `is`(nullValue()))
        assertThat(permission, `is`(nullValue()))
        assertThat(service, `is`(nullValue()))
    }

}