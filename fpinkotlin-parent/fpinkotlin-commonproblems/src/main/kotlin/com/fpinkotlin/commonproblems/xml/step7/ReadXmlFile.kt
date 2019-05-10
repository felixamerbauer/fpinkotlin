package com.fpinkotlin.commonproblems.xml.step7

import com.fpinkotlin.common.List
import com.fpinkotlin.common.Result
import com.fpinkotlin.common.sequence
import org.jdom2.Element
import org.jdom2.JDOMException
import org.jdom2.input.SAXBuilder
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.StringReader

fun <T> readXmlFile(sPath: () -> FilePath,
                    sRootName: () -> ElementName,
                    function: (Element) -> Result<T>,
                    effect: (List<T>) -> Unit): () -> Unit {
    val path = sPath().value
    val rDoc = path.flatMap(::readFile2String)
    val rRoot = sRootName().value
    val result = rDoc.flatMap { doc ->
        rRoot.flatMap { rootElementName ->
            readDocument(rootElementName, doc)
        }
                .flatMap { list -> sequence(list.map(function)) }
    }
    return {
        result.forEach(onSuccess = { effect(it) },
                onFailure = { throw it })
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

val processElement: (List<String>) -> (String) -> (Element) -> Result<String> = { elementNames ->
    { format ->
        { element ->
            try {
                Result(String.format(format, *elementNames.map { getChildText(element, it) }
                        .toArrayList()
                        .toArray()))
            } catch (e: Exception) {
                Result.failure("Exception while formatting element. " +
                        "Probable cause is a missing element name in element list $elementNames")
            }
        }
    }
}

fun getChildText(element: Element, name: String): String =
        element.getChildText(name) ?: "Element $name is not a child of ${element.name}"

data class FilePath private constructor(val value: Result<String>) {

    companion object {

        operator fun invoke(value: String): FilePath =
                FilePath(Result.of({ isValidPath(it) }, value, "Invalid file path: $value"))

        // Replace with validation code
        private fun isValidPath(path: String): Boolean = true
    }
}

class ElementName private constructor(val value: Result<String>) {
    companion object {

        operator fun invoke(value: String): ElementName =
                ElementName(Result.of({ isValidName(it) }, value, "Invalid element name: $value"))

        // Replace with validation code
        private fun isValidName(path: String): Boolean = true
    }
}
