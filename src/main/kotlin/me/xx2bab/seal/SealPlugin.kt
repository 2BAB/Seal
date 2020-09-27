package me.xx2bab.seal

import me.xx2bab.polyfill.Polyfill
import me.xx2bab.polyfill.manifest.source.ManifestAfterMergeListener
import me.xx2bab.polyfill.manifest.source.ManifestBeforeMergeListener
import me.xx2bab.polyfill.manifest.source.ManifestMergeInputProvider
import me.xx2bab.polyfill.manifest.source.ManifestMergeOutputProvider
import org.gradle.api.Plugin
import org.gradle.api.Project

class SealPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val polyfill = Polyfill.createApplicationPolyfill(project)
        val extension = project.extensions.create("seal", SealExtension::class.java)

        polyfill.onVariantProperties { prop ->
            // Before
            val preUpdateTask = project.tasks.register("preUpdate${prop.name.capitalize()}Manifest",
                    ManifestBeforeMergeTask::class.java) {
                it.rules.set(extension.rules)
                it.beforeMergeInputs.set(polyfill.getProvider(prop, ManifestMergeInputProvider::class.java).get())
            }
            val beforeMergeListener = ManifestBeforeMergeListener(preUpdateTask)
            polyfill.addAGPTaskListener(prop, beforeMergeListener)

            // After
            val postUpdateTask = project.tasks.register("postUpdate${prop.name.capitalize()}Manifest",
                    ManifestAfterMergeTask::class.java) {
                it.rules.set(extension.rules)
                it.mergedManifest.set(polyfill.getProvider(prop, ManifestMergeOutputProvider::class.java).get())
            }
            val afterMergeListener = ManifestAfterMergeListener(postUpdateTask)
            polyfill.addAGPTaskListener(prop, afterMergeListener)

            // The official API (which is better obviously) is not gonna work on 4.2.0-alpha12,
            // we may switch back after API is stable.
            /*prop.artifacts.use(manifestUpdater)
                    .wiredWithFiles(ManifestAfterMergeTask::mergedManifest,
                            ManifestAfterMergeTask::updatedManifest)
                    .toTransform(ArtifactType.MERGED_MANIFEST)*/
        }
    }

}