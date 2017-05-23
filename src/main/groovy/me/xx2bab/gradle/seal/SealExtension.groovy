package me.xx2bab.gradle.seal

import org.gradle.api.Project

/**
 * Created by 2bab
 */
class SealExtension {

    Project project

    // Precheck or not
    boolean enabled

    // File Path for AndroidManifest.xml File or Folder include AndroidManifest.xml
    Iterable<String> manifests

    SealExtension(Project project) {
        this.project = project
        enabled = true
        manifests = []
    }
}
