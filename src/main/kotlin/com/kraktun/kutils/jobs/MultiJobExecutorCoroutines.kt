package com.kraktun.kutils.jobs

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class MultiJobExecutorCoroutines(threadPool: Int) : CoroutineScope {

    private val scheduler = Executors.newScheduledThreadPool(threadPool)
    private val tasks = mutableMapOf<String, Job>()
    override val coroutineContext: CoroutineContext
        get() = Job() + scheduler.asCoroutineDispatcher()

    fun registerTask(action: () -> Unit,
                     key: String,
                     interval: Long,
                     initialDelay: Long = 0) {
        if (key in tasks.keys) throw KeyAlreadyUsedException()
        val job = launch(context = coroutineContext) {
            delay(initialDelay)
            while (this.isActive) {
                action()
                delay(interval)
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