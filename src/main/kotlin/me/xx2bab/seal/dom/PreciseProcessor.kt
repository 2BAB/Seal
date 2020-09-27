package me.xx2bab.seal.dom

import me.xx2bab.seal.SealRule
import org.gradle.api.logging.Logger
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

class PreciseProcessor(rules: List<SealRule>, logger: Logger) : GeneralProcessor(rules, logger) {

    override fun findTags(rule: SealRule, dom: Document): NodeList {
        return dom.getElementsByTagName(rule.tag)
    }

    override fun findAttrs(rule: SealRule, tag: Element): Set<Attr> {
        return if (!tag.hasAttribute(rule.attr)) {
            emptySet()
        } else {
            setOf(tag.getAttributeNode(rule.attr) as Attr)
        }
    }

    override fun matchValue(rule: SealRule, attr: Attr): Boolean {
        return attr.value == rule.value
    }

}