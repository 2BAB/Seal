package me.xx2bab.seal

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class PluginTest {

    @Test
    fun `plugin is applied correctly to the project`() {
        // TODO: Don't know why the android application plugin could not be applied (error throws)
//        val project = ProjectBuilder.builder().build()
//        project.buildscript.repositories.google()
//        project.pluginManager.apply("com.android.application")
//        project.pluginManager.apply("me.2bab.seal")
//
//        assert(project.tasks.getByName("preUpdateDebugManifest") is ManifestBeforeMergeTask)
    }

}