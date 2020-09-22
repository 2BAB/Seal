package me.xx2bab.seal

import org.gradle.api.Plugin
import org.gradle.api.Project

class SealPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("seal", SealExtension::class.java)
    }

}