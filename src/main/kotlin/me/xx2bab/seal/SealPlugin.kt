package me.xx2bab.seal

import com.android.build.api.artifact.ArtifactType
import me.xx2bab.polyfill.Polyfill
import me.xx2bab.polyfill.manifest.source.ManifestBeforeMergeListener
import me.xx2bab.polyfill.manifest.source.ManifestMergeInputProvider
import org.gradle.api.Plugin
import org.gradle.api.Project

class SealPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("seal", SealExtension::class.java)
        val polyfill = Polyfill.createApplicationPolyfill(project)

        polyfill.onVariantProperties { prop ->
            // Before
            val preUpdateTask = project.tasks.register("preUpdate${prop.name.capitalize()}Manifest",
                    ManifestBeforeMergeTask::class.java) {
                it.beforeMergeInputs.set(polyfill.getProvider(prop, ManifestMergeInputProvider::class.java).get())
            }
            val beforeMergeListener = ManifestBeforeMergeListener(preUpdateTask)
            polyfill.addAGPTaskListener(prop, beforeMergeListener)

            // After
            val manifestUpdater = project.tasks.register("postUpdate${prop.name.capitalize()}Manifest",
                    ManifestAfterMergeTask::class.java) {}
            prop.artifacts.use(manifestUpdater)
                    .wiredWithFiles(ManifestAfterMergeTask::mergedManifest,
                            ManifestAfterMergeTask::updatedManifest)
                    .toTransform(ArtifactType.MERGED_MANIFEST)
        }
    }

}