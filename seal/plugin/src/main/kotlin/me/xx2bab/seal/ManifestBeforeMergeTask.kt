package me.xx2bab.seal

import me.xx2bab.seal.SealRuleBuilder.HookType
import me.xx2bab.seal.dom.GeneralProcessor
import me.xx2bab.seal.dom.PreciseProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*

abstract class ManifestBeforeMergeTask : DefaultTask() {

    @get:Nested
    abstract val rules: SetProperty<SealRule>

    @get:InputFiles
    abstract val beforeMergeInputs: SetProperty<FileSystemLocation>

    @TaskAction
    fun beforeMerge() {
        val extractRules = rules.get().filter { it.hookType == HookType.BEFORE_MERGE.name }
        val processor: GeneralProcessor = PreciseProcessor(extractRules, project.logger)
        beforeMergeInputs.get().forEach { fsl ->
            val file = fsl.asFile
            processor.process(file, file)
        }
    }
}