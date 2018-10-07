package me.xx2bab.seal

import me.xx2bab.seal.base.Constants
import me.xx2bab.seal.node.AppAttrsExtension
import me.xx2bab.seal.node.AppAttrsPreChecker
import me.xx2bab.seal.replace.AppReplaceValuesExtension
import me.xx2bab.seal.replace.AppReplaceValuesPreChecker
import me.xx2bab.seal.xmlns.XmlnsSweepExtension
import me.xx2bab.seal.xmlns.XmlnsSweeper
import me.xx2bab.seal.base.ManifestPiper
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
                AppAttrsPreChecker appAttrsChecker = new AppAttrsPreChecker(config.remove.enabled,
                        config.remove.attrsShouldRemove)
                AppReplaceValuesPreChecker appReplaceValuesChecker = new AppReplaceValuesPreChecker(config.replace.enabled,
                        config.replace.valuesShouldRemove)

                // add precheck task
                def checkTask = project.task("precheck${variantName}Manifest").doLast {
                    for (manifestFile in manifestFiles) {
                        new ManifestPiper(manifestFile)
                                .pipe(appAttrsChecker)
                                .pipe(appReplaceValuesChecker)
                                .dest(manifestFile)
                    }
                }
                processManifestTask.dependsOn checkTask

                // init postprocessor
                XmlnsSweeper xmlnsSweeper = new XmlnsSweeper(config.xmlns)

                // add postprocessor
                processManifestTask.doLast {
                    processManifestTask.outputs.getFiles().each {
                        def processManifestOutputFilePath = it.absolutePath
                        xmlnsSweeper.sweep(processManifestOutputFilePath)
                    }
                }
            }
        }

    }

    private void initExtension(Project project) {
        config = new GlobalConfig()

        config.plugin = project.extensions.create("seal", SealExtension, project)
        config.remove = project.seal.extensions.create('appAttrs', AppAttrsExtension)
        config.replace = project.seal.extensions.create('appReplaceValues', AppReplaceValuesExtension)
        config.xmlns = project.seal.extensions.create('xmlnsSweep', XmlnsSweepExtension)
    }

}