package me.xx2bab.gradle.seal.node

import groovy.xml.QName
import me.xx2bab.gradle.seal.base.IManifestChecker

/**
 * Created by 2bab
 */
class AppAttrsChecker implements IManifestChecker {

    boolean enabled

    Iterable<String> attrsShouldRemove

    AppAttrsChecker(boolean enabled, Iterable<String> attrsShouldRemove) {
        this.enabled = enabled
        this.attrsShouldRemove = attrsShouldRemove
    }

    @Override
    void check(Node manifestRoot) {
        NodeList applicationNodes = manifestRoot.application
        applicationNodes.each { appNode ->
            Map<QName, String> attrs = appNode.attributes()
            List<QName> removeList = new ArrayList<>()
            attrs.each { name, value ->
                attrsShouldRemove.each { attrShouldRm ->
                    if (name.equals(attrShouldRm)) {
                        removeList.add(name)
                    }
                }
            }
            removeList.each {
                attrs.remove(it)
            }
        }
    }

}
