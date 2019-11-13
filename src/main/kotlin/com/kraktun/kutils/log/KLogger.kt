package com.kraktun.kutils.log

import com.kraktun.kutils.time.TimeFormat
import com.kraktun.kutils.time.getCurrentDateTimeStamp
import com.kraktun.kutils.file.getLocalFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

/**
 * Utility object to log.
 * Must be initialized before usage.
 * Must be closed at the end.
 * TODO Add optional job to periodically flush
 */
object KLogger {

    private lateinit var fileHolder: File
    private lateinit var outPath: String
    @Volatile private var textHolder = StringBuilder()
    private lateinit var timeFormat: TimeFormat
    private var mClass: Class<Any>? = null
    private var initialized = false

    /**
     * Log text
     * @param s text to log
     */
    fun log(s: String) {
        synchronized(this) {
            textHolder.append(s + "\n")
        }
    }

    /**
     * Initialize logger with a custom path and time pattern
     * @param customPath path where to store logs (log files are placed in the provided path, no additional subfolder is created).
     * @param pattern format to use for time tags
     * @return current
     */
    fun initialize(customPath: String, pattern: TimeFormat = TimeFormat.YMD) : KLogger {
        timeFormat = pattern
        fileHolder = File("$customPath/log_${getCurrentDateTimeStamp(pattern)}.log")
        outPath = customPath
        initialized = true
        return this
    }

    /**
     * Initialize logger using as path the folder that contains the passed class.
     * @param c class which will be used to retrieve the path
     * @param type DEFAULT to use path of class c, PARENT to use parent folder of c.
     * @param pattern format to use for time tags
     * @param logFolder subfolder of the extracted path (from c, according to type) where to store logs.
     */
    fun initialize(c: Class<Any>,
                   type: LogFolder = LogFolder.DEFAULT,
                   pattern: TimeFormat = TimeFormat.YMD,
                   logFolder : String = LOG_OUTPUT_FOLDER) : KLogger {
        mClass = c
        timeFormat = pattern
        val mainFolder = when(type) {
            LogFolder.DEFAULT -> getLocalFolder(c).absolutePath
            LogFolder.PARENT -> getLocalFolder(c).parentFile.absolutePath
        }
        File("$mainFolder$logFolder").mkdirs()
        fileHolder = File("$mainFolder$logFolder/log_${getCurrentDateTimeStamp(pattern)}.log")
        outPath = "$mainFolder$logFolder"
        initialized = true
        return this
    }

    /**
     * Writes pending changes.
     * Uses sync method.
     */
    fun close() {
        if (!initialized)
            throw LoggerNotInitializedException("Logger has not been initialized, or it has been closed.")
        synchronized(this) {
            write()
        }
        initialized = false
    }

    /**
     * Writes changes to file. Async IO call.
     */
    fun flush() {
        if (!initialized)
            throw LoggerNotInitializedException("Logger has not been initialized, or it has been closed.")
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                synchronized(this) {
                    write()
                }
            }
        }
    }

    /**
     * Writes logged entries to file.
     */
    private fun write() {
        if (textHolder.isNotEmpty()) {
            FileOutputStream(fileHolder, true).bufferedWriter().use {
                it.write(textHolder.toString())
                it.close()
                textHolder = StringBuilder()
            }
        }
    }
}