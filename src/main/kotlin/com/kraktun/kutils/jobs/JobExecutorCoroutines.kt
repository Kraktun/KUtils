package com.kraktun.kutils.jobs

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class JobExecutorCoroutines(
    val action: () -> Unit,
    private val interval: Long,
    private val initialDelay: Long = 0
) : CoroutineScope {

    private val job = Job()
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
    override val coroutineContext: CoroutineContext
        get() = job + singleThreadExecutor.asCoroutineDispatcher()

    fun stop() {
        job.cancel()
        singleThreadExecutor.shutdown()
    }

    fun start() = launch {
        delay(initialDelay)
        while (this.isActive) {
            action()
            delay(interval)
        }
    }
}