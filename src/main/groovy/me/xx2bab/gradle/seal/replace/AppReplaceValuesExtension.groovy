package me.xx2bab.gradle.seal.replace;

/**
 * Created by 2bab
 */
class AppReplaceValuesExtension {

    boolean enabled

    Iterable<String> valuesShouldRemove

    AppReplaceValuesExtension() {
        enabled = true
        valuesShouldRemove = []
    }
}
