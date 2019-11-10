package com.kraktun.kutils.file

import java.io.File
import java.net.URLDecoder

/**
 * Get parent folder of java\jar file specified.
 * @return path to current parent folder
 * @throws Exception
 */
@Throws(Exception::class)
fun getParentFolder(): String {
    return getCurrentFolder().parentFile.absolutePath
}

/**
 * Get folder of java\jar file specified.
 * @return path to current folder
 * @throws Exception
 */
@Throws(Exception::class)
fun getLocalFolder(): String {
    return getCurrentFolder().absolutePath
}

/**
 * Get folder as file of java\jar file specified.
 * From: https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
 * Edited.
 * @return current folder as file
 * @throws Exception
 */
@Throws(Exception::class)
private fun getCurrentFolder() : File {
    val kClass = com.kraktun.kutils.Main::class.java
    val codeSource = kClass.protectionDomain.codeSource
    val jarFile: File
    if (codeSource.location != null) {
        jarFile = File(codeSource.location.toURI())
    } else {
        val path = kClass.getResource(kClass.simpleName + ".class").path
        var jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"))
        jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8")
        jarFile = File(jarFilePath)
    }
    return jarFile
}