package me.xx2bab.gradle.seal.node;

/**
 * Created by 2bab
 */
class AppAttrsExtension {

    boolean enabled

    Iterable<String> attrsShouldRemove

    AppAttrsExtension() {
        enabled = false
        attrsShouldRemove = []
    }

}
