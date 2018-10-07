package me.xx2bab.seal.replace;

/**
 * Created by 2bab
 */
class AppReplaceValuesExtension {

    boolean enabled

    Iterable<String> valuesShouldRemove

    AppReplaceValuesExtension() {
        enabled = false
        valuesShouldRemove = []
    }
}
