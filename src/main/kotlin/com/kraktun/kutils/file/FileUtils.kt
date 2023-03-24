package com.kraktun.kutils.file

import java.io.File
import java.net.URLDecoder

/**
 * Get folder of java\jar file specified.
 * @return current folder
 * @throws Exception
 */
@Throws(Exception::class)
fun getLocalFolder(c: Class<*>): File {
    return getCurrentFolder(c)
}

/**
 * Get folder as file of java\jar file specified.
 * From: https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
 * Edited.
 * @return current folder as file
 * @throws Exception
 */
@Throws(Exception::class)
private fun getCurrentFolder(kClass: Class<*>): File {
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

@Throws(Exception::class)
fun getTargetFolder(c: Class<*>, buildEnv: BuildEnv): File {
    // For some reason if executed outside C it needs a '.parent' more
    val isInC = getLocalFolder(c).absolutePath.substring(0, 1).equals("C", true) // true if method is executed in C path
    val isJar = getLocalFolder(c).absolutePath.substringAfterLast(".") == "jar"
    val parentJar = getLocalFolder(c).parentFile.absolutePath
    val parentLocal = when {
        !isInC && buildEnv == BuildEnv.INTELLIJ -> getLocalFolder(c).parentFile.parentFile.parent
        buildEnv == BuildEnv.INTELLIJ -> getLocalFolder(c).parentFile.parentFile.parentFile.parent
        else -> getLocalFolder(c).absolutePath
    }
    return if (isJar) {
        File(parentJar)
    } else {
        File(parentLocal)
    }
}

enum class BuildEnv {
    INTELLIJ,
    DEFAULT,
}
