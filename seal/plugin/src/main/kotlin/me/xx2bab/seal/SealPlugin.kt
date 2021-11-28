package me.xx2bab.seal

import com.android.build.api.variant.AndroidComponentsExtension
import me.xx2bab.polyfill.ApplicationVariantPolyfill
import me.xx2bab.polyfill.manifest.source.ManifestBeforeMergeAction
import me.xx2bab.polyfill.manifest.source.ManifestMergeInputProvider
import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.Plugin
import org.gradle.api.Project

class SealPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("seal", SealExtension::class.java)
        val androidExtension = project.extensions.findByType(AndroidComponentsExtension::class.java)!!

        androidExtension.onVariants { variant ->
            val polyfill = ApplicationVariantPolyfill(project, variant)

            // Before merge
            val preUpdateTask = project.tasks.register(
                "preUpdate${variant.name.capitalize()}Manifest",
                ManifestBeforeMergeTask::class.java
            ) {
                it.rules.set(extension.rules)
                it.beforeMergeInputs.set(polyfill.newProvider(ManifestMergeInputProvider::class.java).obtain())
            }
            val beforeMergeAction = ManifestBeforeMergeAction(preUpdateTask)
            polyfill.addAGPTaskAction(beforeMergeAction)

            // After merge
            val postUpdateTask = project.tasks.register(
                "postUpdate${variant.name.capitalize()}Manifest",
                ManifestAfterMergeTask::class.java
            ) {
                it.rules.set(extension.rules)
            }
            variant.artifacts.use(postUpdateTask).wiredWithFiles(
                ManifestAfterMergeTask::mergedManifest,
                ManifestAfterMergeTask::updatedManifest
            ).toTransform(SingleArtifact.MERGED_MANIFEST)
        }
    }

}