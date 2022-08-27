package me.xx2bab.seal

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.seal.SealRuleBuilder.HookType
import me.xx2bab.seal.dom.GeneralProcessor
import me.xx2bab.seal.dom.PreciseProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.logging.Logger
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*

class ManifestBeforeMergeTaskAction(
    private val rules: List<SealRule>,
    private val logger: Logger
    ) : PolyfillAction<List<RegularFile>> {

    override fun onExecute(beforeMergeInputs: Provider<List<RegularFile>>) {
        val extractRules = rules.filter { it.hookType == HookType.BEFORE_MERGE.name }
        val processor: GeneralProcessor = PreciseProcessor(extractRules, logger)
        beforeMergeInputs.get().forEach { fsl ->
            val file = fsl.asFile
            processor.process(file, file)
        }
    }

    override fun onTaskConfigure(task: Task) {
        val rulesJson = Json.encodeToString(rules)
        task.inputs.property("seal.rules", rulesJson)
    }
}