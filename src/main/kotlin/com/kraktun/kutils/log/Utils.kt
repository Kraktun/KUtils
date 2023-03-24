package com.kraktun.kutils.log

import com.kraktun.kutils.time.TimeFormat
import com.kraktun.kutils.time.getCurrentDateTimeLog

/**
 * Print & log functions
 */
fun printlnDTK(tag: String, s: Any = "") {
    println("${getCurrentDateTimeLog(TimeFormat.YMD)} $tag: $s")
    KLogger.logTagged(tag, s.toString(), true)
}

fun printlnDTK(s: Any = "") {
    println("${getCurrentDateTimeLog(TimeFormat.YMD)} : $s")
    KLogger.log(s.toString(), true)
}

fun printlnK(tag: String, s: Any = "") {
    println("${getCurrentDateTimeLog(TimeFormat.YMD)} $tag: $s")
    KLogger.logTagged(tag, s.toString(), false)
}

fun printlnK(s: Any = "") {
    println("${getCurrentDateTimeLog(TimeFormat.YMD)} : $s")
    KLogger.log(s.toString(), false)
}
