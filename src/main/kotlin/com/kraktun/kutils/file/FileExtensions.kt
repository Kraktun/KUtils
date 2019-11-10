package com.kraktun.kutils.file

import java.io.File
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