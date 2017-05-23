package me.xx2bab.gradle.seal.base

/**
 * Created by 2bab
 */
interface IManifestChecker {

    void check(Node manifestRoot)

    boolean isEnabled()

}
