package me.xx2bab.seal.dom

import me.xx2bab.seal.Injector
import me.xx2bab.seal.Injector.sealInfo
import me.xx2bab.seal.SealRule
import me.xx2bab.seal.SealRuleBuilder.DeleteType
import org.gradle.api.logging.Logger
import org.w3c.dom.*
import java.io.File
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

abstract class GeneralProcessor(protected val rules: List<SealRule>,
                                protected val logger: Logger) {

    fun process(inputManifest: File, outputManifest: File) {
        logger.sealInfo("Processing the file: [${inputManifest.absolutePath}]")
        val dom = Injector.newDOMBuilder().parse(inputManifest)
        var matched = false
        rules.forEach b@{ rule ->
            logger.sealInfo("Taking the rule with type ${rule.hookType}: [${rule.ruleName}]")
            if (rule.tag.isBlank()) {
                return@b
            }
            val nodes = findTags(rule, dom) ?: return@b
            for (index in 0 until nodes.length) {
                // Extract
                val node = nodes.item(index)
                if (node.nodeType != Node.ELEMENT_NODE) {
                    continue
                }

                // Tag
                val tag = node as Element
                if (rule.attr.isBlank()) {
                    if (rule.deleteType == DeleteType.TAG.name) {
                        deleteTag(tag)
                        matched = true
                    }
                    continue
                }

                // Attr
                if (tag.attributes.length == 0) {
                    continue
                }
                val attrs = findAttrs(rule, tag)
                if (attrs.isEmpty()) {
                    continue
                }
                if (rule.value.isBlank()) {
                    attrs.forEach { attr ->
                        when (rule.deleteType) {
                            DeleteType.TAG.name -> {
                                deleteTag(tag)
                            }
                            DeleteType.ATTR.name -> {
                                deleteAttr(tag, attr)
                            }
                        }
                    }
                    matched = true
                    continue
                }

                // Value
                attrs.forEach { attr ->
                    if (matchValue(rule, attr)) {
                        when (rule.deleteType) {
                            DeleteType.TAG.name -> {
                                deleteTag(tag)
                            }
                            DeleteType.ATTR.name -> {
                                deleteAttr(tag, attr)
                            }
                        }
                        matched = true
                    }
                }
            }
        }

        // Output
        if (matched) {
            val transformer = Injector.newTransformer()
            val domSource = DOMSource(dom)
            val streamResult = StreamResult(outputManifest)
            transformer.transform(domSource, streamResult)
        }
    }

    private fun deleteTag(tag: Element) {
        tag.parentNode.removeChild(tag)
        logger.sealInfo("Deleted tag: [${tag.tagName}]")
    }

    private fun deleteAttr(tag: Element, attr: Attr) {
        tag.removeAttribute(attr.name)
        logger.sealInfo("Deleted attr: [${tag.tagName} - ${attr.name}]")
    }

    protected abstract fun findTags(rule: SealRule, dom: Document): NodeList?

    protected abstract fun findAttrs(rule: SealRule, tag: Element): Set<Attr>

    protected abstract fun matchValue(rule: SealRule, attr: Attr): Boolean

}