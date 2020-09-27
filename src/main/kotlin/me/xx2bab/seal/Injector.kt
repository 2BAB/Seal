package me.xx2bab.seal

import org.gradle.api.logging.Logger
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory

object Injector {

    private const val LOG_TAG = "[Seal]: "

    private val documentBuilderFactory = DocumentBuilderFactory.newInstance()
    private val transformer= TransformerFactory.newInstance().newTransformer()

    fun newDOMBuilder(): DocumentBuilder {
        return documentBuilderFactory.newDocumentBuilder()
    }

    fun newTransformer(): Transformer {
        return transformer
    }

    fun Logger.sealInfo(message: String) {
        this.info(LOG_TAG + message)
    }

}