package com.fpinkotlin.commonproblems.xml.step3

import com.fpinkotlin.common.List
import com.fpinkotlin.common.Result
import org.jdom2.Element
import org.jdom2.JDOMException
import org.jdom2.input.SAXBuilder
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.StringReader

fun readXmlFile(sPath: () -> Result<String>,
                sRootName: () -> Result<String>,
                format: Pair<String, List<String>>,
                effect: (List<String>) -> Unit): () -> Unit {
    val path = sPath()
    val rDoc = path.flatMap(::readFile2String)
    val rRoot = sRootName()
    val result = rDoc.flatMap { doc ->
        rRoot.flatMap { rootElementName ->
            readDocument(rootElementName, doc)
        }
                .map { list -> toStringList(list, format) }
    }
    return {
        result.forEach(onSuccess = { effect(it) },
                onFailure = { it.printStackTrace() })
    }
}

fun readFile2String(path: String): Result<String> =
        try {
            FileInputStream(File(path)).use {
                it.bufferedReader().use {
                    Result(it.readText())
                }
            }
        } catch (e: IOException) {
            Result.failure("IOException while reading file $path: ${e.message}")
        } catch (e: Exception) {
            Result.failure("Unexpected error while reading file $path: ${e.message}")
        }

fun readDocument(rootElementName: String, stringDoc: String): Result<List<Element>> =
        SAXBuilder().let { builder ->
            try {
                val document = builder.build(StringReader(stringDoc))
                val rootElement = document.rootElement
                Result(List(*rootElement.getChildren(rootElementName).toTypedArray()))
            } catch (io: IOException) {
                Result.failure("Invalid root element name '$rootElementName' or XML data $stringDoc: ${io.message}")
            } catch (jde: JDOMException) {
                Result.failure("Invalid root element name '$rootElementName' or XML data $stringDoc: ${jde.message}")
            } catch (e: Exception) {
                Result.failure("Unexpected error while reading XML data $stringDoc: ${e.message}")
            }
        }

fun toStringList(list: List<Element>, format: Pair<String, List<String>>): List<String> =
        list.map { e -> processElement(e, format) }

fun processElement(element: Element, format: Pair<String, List<String>>): String { // <4>
    val formatString = format.first
    val parameters = format.second.map { element.getChildText(it) }
    return String.format(formatString, *parameters.toArrayList().toArray())
}
