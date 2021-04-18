package me.xx2bab.seal

import me.xx2bab.seal.SealRuleBuilder.HookType
import me.xx2bab.seal.dom.GeneralProcessor
import me.xx2bab.seal.dom.PreciseProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

abstract class ManifestAfterMergeTask : DefaultTask() {

    @get:Input
    abstract val rules: SetProperty<SealRule>

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

//    @get:OutputFile
//    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun afterMerge() {
        val extractRules = rules.get().filter { it.hookType == HookType.AFTER_MERGE.name }
        val file = mergedManifest.asFile.get()
        val processor: GeneralProcessor = PreciseProcessor(extractRules, project.logger)
        processor.process(file, file)
    }

}
