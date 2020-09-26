package me.xx2bab.seal

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory

object Injector {

    private val documentBuilderFactory = DocumentBuilderFactory.newInstance()
    private val transformer= TransformerFactory.newInstance().newTransformer()

    fun newDOMBuilder(): DocumentBuilder {
        return documentBuilderFactory.newDocumentBuilder()
    }

    fun newTransformer(): Transformer {
        return transformer
    }

}