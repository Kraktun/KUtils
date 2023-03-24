package com.kraktun.kutils.jobs

import java.lang.Runnable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class MultiJobExecutor(threadPool: Int) {

    private val scheduler = Executors.newScheduledThreadPool(threadPool)
    private val tasks = mutableMapOf<String, ScheduledFuture<*>>()

    fun registerTask(
        action: () -> Unit,
        key: String,
        interval: Long,
        initialDelay: Long = 0,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
    ) {
        if (key in tasks.keys) throw KeyAlreadyUsedException()
        val task = Runnable { action.invoke() }
        val future = scheduler.scheduleWithFixedDelay(task, initialDelay, interval, timeUnit)
        tasks[key] = future
    }

    fun stopTask(key: String, stopIfRunning: Boolean = false): Boolean {
        if (key in tasks.keys) {
            val t = tasks.remove(key)
            return t!!.cancel(stopIfRunning)
        }
        return false
    }

    fun stopALl() {
        scheduler.shutdown()
    }
}
