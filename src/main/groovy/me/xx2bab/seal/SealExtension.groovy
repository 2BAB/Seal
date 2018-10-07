package me.xx2bab.seal

import org.gradle.api.Project

/**
 * Created by 2bab
 */
class SealExtension {

    Project project

    // check or not
    boolean enabled

    SealExtension(Project project) {
        this.project = project
        enabled = true
    }
}
