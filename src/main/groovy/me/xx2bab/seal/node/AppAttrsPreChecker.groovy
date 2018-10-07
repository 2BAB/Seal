package me.xx2bab.seal.node

import groovy.xml.QName
import me.xx2bab.seal.base.IManifestPreChecker

/**
 * Created by 2bab
 */
class AppAttrsPreChecker implements IManifestPreChecker {

    boolean enabled

    Iterable<String> attrsShouldRemove

    AppAttrsPreChecker(boolean enabled, Iterable<String> attrsShouldRemove) {
        this.enabled = enabled
        this.attrsShouldRemove = attrsShouldRemove
    }

    @Override
    void check(Node manifestRoot) {
        if (!enabled) {
            return
        }

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
