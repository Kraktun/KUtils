package com.kraktun.kutils.jobs

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class MultiJobExecutorCoroutines(threadPool: Int) : CoroutineScope {

    private val scheduler = Executors.newScheduledThreadPool(threadPool)
    private val tasks = mutableMapOf<String, Job>()
    override val coroutineContext: CoroutineContext
        get() = Job() + scheduler.asCoroutineDispatcher()

    fun registerTask(action: (CoroutineScope) -> Unit,
                     key: String,
                     interval: Long,
                     initialDelay: Long = 0,
                     timeUnit: TimeUnit = TimeUnit.SECONDS) {
        if (key in tasks.keys) throw KeyAlreadyUsedException()
        val targetUnit = TimeUnit.MILLISECONDS
        val job = CoroutineScope(Dispatchers.IO).launch(context = coroutineContext) {
            delay(targetUnit.convert(initialDelay, timeUnit))
            while (isActive) {
                action(this)
                delay(targetUnit.convert(interval, timeUnit))
            }
        }
        tasks[key] = job
    }

    fun stopTask(key: String) {
        if (key in tasks.keys) {
            val t = tasks.remove(key)
            t?.cancel()
        }
    }

    fun stopALl() {
        scheduler.shutdown()
    }
}