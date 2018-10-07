package me.xx2bab.seal.base

/**
 * Created by 2bab
 */
interface IManifestPreChecker {

    void check(Node manifestRoot)

    boolean isEnabled()

}
