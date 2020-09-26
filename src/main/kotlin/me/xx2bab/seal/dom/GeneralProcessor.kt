package me.xx2bab.seal.dom

import me.xx2bab.seal.Injector
import me.xx2bab.seal.SealRule
import me.xx2bab.seal.SealRuleBuilder.DeleteType
import org.w3c.dom.*
import java.io.File
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


abstract class GeneralProcessor(protected val rules: List<SealRule>) {

    fun process(inputManifest: File, outputManifest: File) {
        val dom = Injector.newDOMBuilder().parse(inputManifest)
        var matched = false
        rules.forEach b@{ rule ->
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
                        tag.parentNode.removeChild(tag)
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
                                tag.parentNode.removeChild(tag)
                            }
                            DeleteType.ATTR.name -> {
                                tag.removeAttribute(attr.name)
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
                                tag.parentNode.removeChild(tag)
                            }
                            DeleteType.ATTR.name -> {
                                tag.removeAttribute(attr.name)
                            }
                            DeleteType.VALUE.name -> {
                                tag.setAttribute(rule.attr, "")
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

    protected abstract fun findTags(rule: SealRule, dom: Document): NodeList?

    protected abstract fun findAttrs(rule: SealRule, tag: Element): Set<Attr>

    protected abstract fun matchValue(rule: SealRule, attr: Attr): Boolean

}