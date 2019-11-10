package com.kraktun.kutils.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Get current date and time formatted according to passed pattern
 */
fun getCurrentDateTimeStamp(pattern: TimeFormat): String {
    val patternF = when(pattern) {
        TimeFormat.YDM -> "yyyy-dd-MM_HH-mm-ss"
        TimeFormat.YMD -> "yyyy-MM-dd_HH-mm-ss"
    }
    return LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern(patternF))
}