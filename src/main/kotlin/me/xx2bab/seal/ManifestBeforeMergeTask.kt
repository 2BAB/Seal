package me.xx2bab.seal

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

abstract class ManifestBeforeMergeTask : DefaultTask() {

    @get:InputFiles
    abstract val beforeMergeInputs: SetProperty<FileSystemLocation>

    @TaskAction
    fun beforeMerge() {

    }
}