package me.xx2bab.seal

import me.xx2bab.seal.SealRuleBuilder.HookType
import me.xx2bab.seal.dom.GeneralProcessor
import me.xx2bab.seal.dom.PreciseProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*

abstract class ManifestAfterMergeTask : DefaultTask() {

    @get:Nested
    abstract val rules: SetProperty<SealRule>

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun afterMerge() {
        val extractRules = rules.get().filter { it.hookType == HookType.AFTER_MERGE.name }
        val inputFile = mergedManifest.asFile.get()
        val outputFile = updatedManifest.asFile.get()
        val processor: GeneralProcessor = PreciseProcessor(extractRules, project.logger)
        processor.process(inputFile, outputFile)
    }

}
