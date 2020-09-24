package me.xx2bab.seal

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ManifestAfterMergeTask : DefaultTask() {

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun afterMerge() {
//        var manifest = mergedManifest.asFile.get().readText()
//        manifest = manifest.replace("android:versionCode=\"1\"", "android:versionCode=\"2\"")
//        println("Writes to " + updatedManifest.get().asFile.getAbsolutePath())
//        updatedManifest.get().asFile.writeText(manifest)
    }

}
