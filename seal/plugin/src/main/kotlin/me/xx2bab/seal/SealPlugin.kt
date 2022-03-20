package me.xx2bab.seal

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import me.xx2bab.polyfill.*

class SealPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply(plugin = "me.2bab.polyfill")
        val extension = project.extensions.create<SealExtension>("seal")
        val androidExtension =
            project.extensions.findByType(ApplicationAndroidComponentsExtension::class.java)!!

        androidExtension.onVariants { variant ->
            // Before merge
            val preUpdateTask = project.tasks.register<ManifestBeforeMergeTask>(
                "preUpdate${variant.name.capitalize()}Manifest",
            ) {
                rules.set(extension.rules)
            }
            variant.artifactsPolyfill.use(
                taskProvider = preUpdateTask,
                wiredWith = ManifestBeforeMergeTask::beforeMergeInputs,
                toInPlaceUpdate = PolyfilledMultipleArtifact.ALL_MANIFESTS
            )

            // After merge
            val postUpdateTask = project.tasks.register<ManifestAfterMergeTask>(
                "postUpdate${variant.name.capitalize()}Manifest"
            ) {
                rules.set(extension.rules)
            }
            variant.artifacts.use(postUpdateTask)
                .wiredWithFiles(ManifestAfterMergeTask::mergedManifest,
                    ManifestAfterMergeTask::updatedManifest)
                .toTransform(SingleArtifact.MERGED_MANIFEST)
        }
    }

}