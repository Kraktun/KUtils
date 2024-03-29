package com.kraktun.kutils.log

import com.kraktun.kutils.file.BuildEnv
import com.kraktun.kutils.file.getTargetFolder
import com.kraktun.kutils.jobs.SingleJobExecutor
import com.kraktun.kutils.time.TimeFormat
import com.kraktun.kutils.time.getCurrentDateTimeLog
import com.kraktun.kutils.time.getCurrentDateTimeStamp
import kotlinx.coroutines.*
import java.io.*
import java.util.concurrent.TimeUnit

/**
 * Utility object to log.
 * Must be initialized before usage.
 * Must be closed at the end.
 */
object KLogger {

    private lateinit var fileHolder: File
    private lateinit var outPath: String

    @Volatile private var textHolder = StringBuilder()
    private lateinit var timeFormat: TimeFormat
    private var mClass: Class<*>? = null
    private var initialized = false
    private var cleanerJob: SingleJobExecutor? = null
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    /**
     * Log text
     * @param s text to log
     * @param addDateTime true if current date-time should pre-pended to s
     */
    fun log(s: String, addDateTime: Boolean = false) {
        synchronized(this) {
            val text = if (addDateTime) "[${getCurrentDateTimeLog(timeFormat)}] $s\n" else "$s\n"
            textHolder.append(text)
        }
    }

    /**
     * Log text with tag
     * @param tag tag to add in front of s
     * @param s text to log
     * @param addDateTime true if current date-time should pre-pended to tag + s
     */
    fun logTagged(tag: String, s: String, addDateTime: Boolean = false) {
        synchronized(this) {
            val text = if (addDateTime) "[${getCurrentDateTimeLog(timeFormat)}] $tag: $s\n" else "$s\n"
            textHolder.append(text)
        }
    }

    /**
     * Get current log file
     * @return current log file
     */
    fun getOutputFile(): File {
        return fileHolder
    }

    /**
     * Initialize logger with a custom path and time pattern
     * @param customPath path where to store logs (log files are placed in the provided path, no additional subfolder is created).
     * @param pattern format to use for time tags
     * @return current
     */
    fun initialize(customPath: String, pattern: TimeFormat = TimeFormat.YMD): KLogger {
        synchronized(this) {
            timeFormat = pattern
            fileHolder = File("$customPath/log_${getCurrentDateTimeStamp(pattern)}.log")
            outPath = customPath
            initialized = true
        }
        return this
    }

    /**
     * Initialize logger using as path the folder that contains the passed class.
     * @param c class which will be used to retrieve the path
     * @param type DEFAULT to use path of class c, PARENT to use parent folder of c.
     * @param pattern format to use for time tags
     * @param logFolder subfolder of the extracted path (from c, according to type) where to store logs.
     * @param buildEnv build environment (needed to match the right number of parent folders if executed from an IDE).
     *      Used only if not executed from a jar file.
     */
    fun initialize(
        c: Class<*>,
        type: LogFolder = LogFolder.DEFAULT,
        pattern: TimeFormat = TimeFormat.YMD,
        logFolder: String = LOG_OUTPUT_FOLDER,
        buildEnv: BuildEnv,
    ): KLogger {
        synchronized(this) {
            mClass = c
            timeFormat = pattern
            val mainFolder = when (type) {
                LogFolder.DEFAULT -> getTargetFolder(c, buildEnv).absolutePath
                LogFolder.PARENT -> getTargetFolder(c, buildEnv).parentFile.absolutePath
            }
            outPath = "$mainFolder/$logFolder"
            File(outPath).mkdirs()
            fileHolder = File("$outPath/log_${getCurrentDateTimeStamp(pattern)}.log")
            initialized = true
        }
        return this
    }

    /**
     * Add job to periodically flush the writer.
     */
    fun withExecutor(interval: Long = 60, unit: TimeUnit = TimeUnit.SECONDS): KLogger {
        synchronized(this) {
            cleanerJob = SingleJobExecutor(
                action = { flush() },
                interval = interval,
                timeUnit = unit,
                initialDelay = interval,
            ).also { it.start() }
        }
        return this
    }

    /**
     * Writes pending changes.
     * Uses sync method.
     */
    fun close() {
        synchronized(this) {
            cleanerJob?.stop()
            scope.cancel()
            write()
            initialized = false
        }
    }

    /**
     * Writes changes to file. Async IO call.
     */
    fun flush() {
        scope.launch(CoroutineName("KLogger flush")) {
            synchronized(this) {
                write()
            }
        }
    }

    /**
     * Writes logged entries to file.
     * Must be executed only in synchronized blocks.
     */
    private fun write() {
        if (!initialized) {
            throw LoggerNotInitializedException("Logger has not been initialized, or it has been closed.")
        }
        if (textHolder.isNotEmpty()) {
            FileOutputStream(fileHolder, true).bufferedWriter().use {
                it.write(textHolder.toString())
                it.close()
                textHolder = StringBuilder()
            }
        }
    }
}
