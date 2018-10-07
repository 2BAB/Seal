package me.xx2bab.seal

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.tasks.MergeManifests
import me.xx2bab.seal.base.Constants
import me.xx2bab.seal.base.ManifestPiper
import me.xx2bab.seal.node.AppAttrsExtension
import me.xx2bab.seal.node.AppAttrsPreChecker
import me.xx2bab.seal.replace.AppReplaceValuesExtension
import me.xx2bab.seal.replace.AppReplaceValuesPreChecker
import me.xx2bab.seal.xmlns.XmlnsSweepExtension
import me.xx2bab.seal.xmlns.XmlnsSweeper
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by 2bab
 */
class SealPlugin implements Plugin<Project> {

    private GlobalConfig config

    void apply(Project project) {
        if (!project.plugins.hasPlugin(AppPlugin.class)) {
            throw new IllegalStateException("'com.android.application' plugin required.")
        }

        initExtension(project)

        project.afterEvaluate {

            if (!config.plugin.enabled) {
                println(Constants.TAG + ": Plugin is disabled.")
                return
            }

            project.extensions.android.applicationVariants.all { variant ->
                String variantName = variant.name.capitalize()
                variant.outputs.each { output ->
                    MergeManifests processManifestTask = output.processManifest
                    // processManifestTask.outputs.upToDateWhen { false }

                    // PreCheckers
                    processManifestTask.doFirst("preCheck${variantName}Manifest") {
                        AppAttrsPreChecker appAttrsChecker = new
                                AppAttrsPreChecker(config.remove.enabled,
                                config.remove.attrsShouldRemove)
                        AppReplaceValuesPreChecker appReplaceValuesChecker = new
                                AppReplaceValuesPreChecker(config.replace.enabled,
                                config.replace.valuesShouldRemove)
                        def manifestFiles = processManifestTask.manifests.files
                        for (manifestFile in manifestFiles) {
                            new ManifestPiper(manifestFile)
                                    .pipe(appAttrsChecker)
                                    .pipe(appReplaceValuesChecker)
                                    .dest(manifestFile)
                        }
                    }

                    // PostProcessors
                    XmlnsSweeper xmlnsSweeper = new XmlnsSweeper(config.xmlns)
                    processManifestTask.doLast("postProcess${variantName}Manifest") {
                        processManifestTask.outputs.getFiles().each {
                            def processManifestOutputFilePath = it.absolutePath
                            xmlnsSweeper.sweep(processManifestOutputFilePath)
                        }
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