package me.xx2bab.seal

import me.xx2bab.polyfill.manifest.bytes.parser.ManifestBytesTweaker
import net.lingala.zip4j.ZipFile
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class SealBaseFunctionTest {

    companion object {

        private const val testProjectPath = "../../"
        private const val testProjectJsonOutputPath = "${testProjectPath}/build/functionTestOutput"
        private const val testProjectAppOutputPath = "${testProjectPath}/test-app/build/outputs/apk/debug"
        private const val testProjectAppUnzipPath = "${testProjectPath}/test-app/build/outputs/apk/debug/unzipped"
        lateinit var manifestBytesTweaker: ManifestBytesTweaker

        @BeforeClass
        @JvmStatic
        fun buildTestProject() {
            println("Building...")
            GradleRunner.create()
                    .forwardOutput()
//                    .withPluginClasspath()
                    .withArguments("clean", "assembleDebug", "--stacktrace")
                    .withProjectDir(File(testProjectPath))
                    .build()

            println("Unzipping...")
            unzipApk()
            constructManifestTweaker()

            println("Testing...")
        }

        private fun unzipApk() {
            File(testProjectAppOutputPath)
                    .walk()
                    .filter { it.extension == "apk" }
                    .first {
                        val unzipFolder = File(testProjectAppUnzipPath)
                        if (!unzipFolder.exists()) {
                            unzipFolder.mkdir()
                            ZipFile(it.absolutePath).extractAll(unzipFolder.absolutePath)
                        }
                        true
                    }
        }

        private fun constructManifestTweaker() {
            val extractedAndroidManifest = File(testProjectAppUnzipPath, "AndroidManifest.xml")
            Assert.assertTrue(extractedAndroidManifest.exists())
            manifestBytesTweaker = ManifestBytesTweaker()
            manifestBytesTweaker.read(extractedAndroidManifest)
        }

        fun Boolean.toInt() = if (this) 1 else 0
    }

    @Test
    fun manifestBeforeMerge_deleteAttrsSuccessfully() {
        val applicationTag = manifestBytesTweaker.getSpecifyStartTagBodyByName("application")
        Assert.assertNotNull(applicationTag)
        val allowBackup = manifestBytesTweaker.getAttrFromTagAttrs(applicationTag!!, "description")
        Assert.assertNull(allowBackup)
        val replace = manifestBytesTweaker.getAttrFromTagAttrs(applicationTag, "replace")
        Assert.assertNull(replace)
    }

    @Test
    fun manifestAfterMerge_deleteTagSuccessfully() {
        val feature = manifestBytesTweaker.getSpecifyStartTagBodyByName("uses-feature")
        val permission = manifestBytesTweaker.getSpecifyStartTagBodyByName("permission")
        val service = manifestBytesTweaker.getSpecifyStartTagBodyByName("service")
        Assert.assertNull(feature)
        Assert.assertNull(permission)
        Assert.assertNull(service)
    }

}