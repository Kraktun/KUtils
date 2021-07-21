package com.kraktun.kutils.jobs

import java.lang.Runnable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class JobExecutor(
    val action: () -> Unit,
    val interval: Long,
    val initialDelay: Long = 0,
    val timeUnit: TimeUnit
)  {

    private val scheduler = Executors.newScheduledThreadPool(1)
    private val task = Runnable { action }

    fun stop() {
        scheduler.shutdown()
    }

    fun start() {
        scheduler.scheduleWithFixedDelay(task, initialDelay, interval, timeUnit)
    }
}