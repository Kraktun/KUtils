package com.kraktun.kutils.log

import com.kraktun.kutils.time.TimeFormat
import com.kraktun.kutils.time.getCurrentDateTimeStamp
import com.kraktun.kutils.file.getLocalFolder
import com.kraktun.kutils.file.getParentFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

object KLogger {

    private lateinit var fileHolder: File
    private lateinit var outPath: String
    @Volatile private var textHolder = StringBuilder()
    private lateinit var timeFormat: TimeFormat
    private var mClass: Class<Any>? = null
    private var initialized = false

    fun log(s: String) {
        synchronized(this) {
            textHolder.append(s + "\n")
        }
    }

    fun initialize(customPath: String, pattern: TimeFormat = TimeFormat.YMD) {
        timeFormat = pattern
        fileHolder = File("$customPath/log_${getCurrentDateTimeStamp(pattern)}.log")
        outPath = customPath
        initialized = true
    }

    fun initialize(c: Class<Any>, type: LogFolder = LogFolder.DEFAULT, pattern: TimeFormat = TimeFormat.YMD) {
        mClass = c
        timeFormat = pattern
        val mainFolder = when(type) {
            LogFolder.DEFAULT -> getLocalFolder(c)
            LogFolder.PARENT -> getParentFolder(c)
        }
        fileHolder = File("$mainFolder$LOG_OUTPUT_FOLDER/log_${getCurrentDateTimeStamp(pattern)}.log")
        outPath = "$mainFolder$LOG_OUTPUT_FOLDER"
        initialized = true
    }

    fun flush() {
        if (!initialized)
            return // TODO Exception
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                synchronized(this) {
                    if (textHolder.isNotEmpty()) {
                        FileOutputStream(fileHolder, true).bufferedWriter().use {
                            it.write(textHolder.toString())
                            it.close()
                            textHolder = StringBuilder()
                        }
                    }
                }
            }
        }
    }
}