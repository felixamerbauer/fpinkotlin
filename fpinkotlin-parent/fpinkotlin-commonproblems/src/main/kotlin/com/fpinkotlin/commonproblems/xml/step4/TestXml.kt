package com.fpinkotlin.commonproblems.xml.step4

import com.fpinkotlin.common.List

fun <A> processList(list: List<A>) = list.forEach(::println)

fun getRootElementName(): ElementName = ElementName("staff") // Simulating a computation that may fail.

fun getXmlFilePath(): FilePath = FilePath("/path/to/file.xml") // <- adjust path

private val format = Pair("First Name : %s\n" +
        "\tLast Name : %s\n" +
        "\tEmail : %s\n" +
        "\tSalary : %s", List("firstName", "lastName", "email", "salary"))

fun main(args: Array<String>) {
    val program = readXmlFile(::getXmlFilePath,
            ::getRootElementName,
            format,
            ::processList)
    program()
}
