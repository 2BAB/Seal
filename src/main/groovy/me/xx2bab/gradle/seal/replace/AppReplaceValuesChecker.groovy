package me.xx2bab.gradle.seal.replace

import me.xx2bab.gradle.seal.base.Constants
import me.xx2bab.gradle.seal.base.IManifestChecker

/**
 * Created by 2bab
 */
class AppReplaceValuesChecker implements IManifestChecker {

    boolean enabled

    Iterable<String> valuesShouldRemove

    AppReplaceValuesChecker(boolean enabled, Iterable<String> valuesShouldRemove) {
        this.enabled = enabled
        this.valuesShouldRemove = valuesShouldRemove
    }

    @Override
    void check(Node manifestRoot) {
        def applicationNodes = manifestRoot.application
        applicationNodes.each { appNode ->
            String replaceValue = appNode.attribute(Constants.NS_TOOLS.replace).toString()
            if (replaceValue == null || replaceValue == "null") {
                return
            }

            String[] replaceList = replaceValue.split(",")
            def filterList = []

            replaceList.each { replaceEachValue ->
                boolean isTarget = false
                valuesShouldRemove.each { f_ckAttr ->
                    if (replaceEachValue.contains(f_ckAttr)) {
                        isTarget = true
                    }
                }
                if (!isTarget) {
                    filterList << replaceEachValue
                }
            }

            if (filterList.empty) {
                appNode.attributes().remove(Constants.NS_TOOLS.replace)
            } else {
                String filterResult = filterList.join(",")
                appNode.attributes().put(Constants.NS_TOOLS.replace, filterResult)
            }
        }
    }
}
