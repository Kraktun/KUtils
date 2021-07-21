package com.kraktun.kutils.file

import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Execute script with passed arguments in custom amount of time
 */
fun File.executeScript(
    timeoutAmount: Long,
    timeoutUnit: TimeUnit,
    vararg arguments: String
): String {
    val process = ProcessBuilder(*arguments)
        .directory(this)
        .start()
        .apply { waitFor(timeoutAmount, timeoutUnit) }

    if (process.exitValue() != 0) {
        return process.errorStream.bufferedReader().readText().substringBeforeLast("\n")
    }
    return process.inputStream.bufferedReader().readText().substringBeforeLast("\n")
}

/**
 * Execute script with passed arguments in fixed amount of time
 */
fun File.executeScript(vararg arguments: String): String {
    return this.executeScript(10, TimeUnit.SECONDS, *arguments)
}

/**
 * Write text to current file
 * @param s string to write
 * @param append true if you want to append text to the file, false to overwrite
 * @param async true if it should be executed in a IO coroutine
 */
fun File.writeText(s: String, append: Boolean = false, async: Boolean = true) {
    if (s.isEmpty())
        return
    if (async) {
        runBlocking {
            coroutineScope {
                launch {
                    withContext(Dispatchers.IO) {
                        writeFile(this@writeText, s, append)
                    }
                }
            }
        }
    } else {
        writeFile(this, s, append)
    }
}

/**
 * Write text to file
 * @param f file where to write
 * @param s text to write
 * @param append true to append text
 */
private fun writeFile(f: File, s: String, append: Boolean) {
    FileOutputStream(f, append).bufferedWriter().use {
        it.write(s)
        it.close()
    }
}