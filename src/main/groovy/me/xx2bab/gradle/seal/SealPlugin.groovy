package me.xx2bab.gradle.seal


import com.android.build.gradle.AppPlugin
import me.xx2bab.gradle.seal.base.Constants
import me.xx2bab.gradle.seal.base.ManifestPiper
import me.xx2bab.gradle.seal.node.AppAttrsExtension
import me.xx2bab.gradle.seal.node.AppAttrsPreChecker
import me.xx2bab.gradle.seal.replace.AppReplaceValuesExtension
import me.xx2bab.gradle.seal.replace.AppReplaceValuesPreChecker
import me.xx2bab.gradle.seal.xmlns.XmlnsSweepExtension
import me.xx2bab.gradle.seal.xmlns.XmlnsSweeper
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

                // init checkers
                AppAttrsPreChecker appAttrsChecker = new AppAttrsPreChecker(config.remove.enabled,
                        config.remove.attrsShouldRemove)
                AppReplaceValuesPreChecker appReplaceValuesChecker = new AppReplaceValuesPreChecker(config.replace.enabled,
                        config.replace.valuesShouldRemove)

                // init postprocessor
                XmlnsSweeper xmlnsSweeper = new XmlnsSweeper(config.xmlns)

                variant.outputs.all { output ->
                    def processManifestTask = output.processManifestProvider.get()

                    processManifestTask.doFirst("precheck${variantName}Manifest") {

                        def manifestFiles = processManifestTask.manifests.files


                        for (manifestFile in manifestFiles) {
                            // filtrate useless manifestFile
                            if (manifestFile.absolutePath.indexOf(project.rootDir.toString()) < 0)
                                break

                            new ManifestPiper(manifestFile)
                                    .pipe(appAttrsChecker)
                                    .pipe(appReplaceValuesChecker)
                                    .dest(manifestFile)
                        }
                    }

                    processManifestTask.doLast("postProcess${variantName}Manifest") {
                        File files = processManifestTask.manifestOutputDirectory.getAsFile().get()
                        if (files.isDirectory()) {
                            files.listFiles().each { file ->
                                xmlnsSweeper.sweep(file)
                            }
                        } else {
                            xmlnsSweeper.sweep(files)
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