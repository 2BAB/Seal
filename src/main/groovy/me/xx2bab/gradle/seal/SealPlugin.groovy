package me.xx2bab.gradle.seal

import com.android.build.gradle.AppPlugin
import me.xx2bab.gradle.seal.base.Constants
import me.xx2bab.gradle.seal.base.ManifestPiper
import me.xx2bab.gradle.seal.node.AppAttrsExtension
import me.xx2bab.gradle.seal.node.AppAttrsChecker
import me.xx2bab.gradle.seal.replace.AppReplaceValuesExtension
import me.xx2bab.gradle.seal.replace.AppReplaceValuesChecker
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileTree

/**
 * Created by 2bab
 */
class SealPlugin implements Plugin<Project> {

    private GlobalConfig config
    private FileTree manifestFiles

    void apply(Project project) {

        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new IllegalStateException("'com.android.application' plugin required.")
        }

        initExtension(project)

        project.afterEvaluate {

            if (!config.plugin.enabled || config.plugin.manifests.size() == 0) {
                println(Constants.TAG + ": Plugin is disabled.")
                return
            }

            config.plugin.manifests.each {
                def eachFolder = project.fileTree(it) {
                    include '**/AndroidManifest.xml'
                }
                if (manifestFiles == null) {
                    manifestFiles = eachFolder
                } else {
                    manifestFiles += eachFolder
                }
            }

            project.extensions.android.applicationVariants.all { variant ->
                // find seal process task
                String variantName = variant.name.capitalize()
                Task processManifestTask = project.tasks["process${variantName}Manifest"]
                processManifestTask.outputs.upToDateWhen { false }

                // init checkers
                AppAttrsChecker appAttrsChecker = new AppAttrsChecker(config.remove.enabled,
                        config.remove.attrsShouldRemove)
                AppReplaceValuesChecker appReplaceValuesChecker = new AppReplaceValuesChecker(config.replace.enabled,
                        config.replace.valuesShouldRemove)

                // add precheck task before
                def checkTask = project.task("precheck${variantName}Manifest").doLast {
                    for (manifestFile in manifestFiles) {
                        new ManifestPiper(manifestFile)
                                .pipe(appAttrsChecker)
                                .pipe(appReplaceValuesChecker)
                                .dest(manifestFile)
                    }
                }
                processManifestTask.dependsOn checkTask
            }
        }

    }

    private void initExtension(Project project) {
        config = new GlobalConfig()

        config.plugin = project.extensions.create("seal", SealExtension, project)
        config.remove = project.seal.extensions.create('appAttrs', AppAttrsExtension)
        config.replace = project.seal.extensions.create('appReplaceValues', AppReplaceValuesExtension)
    }

}