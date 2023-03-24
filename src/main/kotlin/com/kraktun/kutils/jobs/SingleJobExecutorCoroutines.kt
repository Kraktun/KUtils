package com.kraktun.kutils.jobs

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class SingleJobExecutorCoroutines(
    val action: (CoroutineScope) -> Unit,
    private val interval: Long,
    private val initialDelay: Long = 0,
    private val timeUnit: TimeUnit = TimeUnit.SECONDS,
) : CoroutineScope {

    private val job = Job()
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
    override val coroutineContext: CoroutineContext
        get() = job + singleThreadExecutor.asCoroutineDispatcher()
    private val targetUnit = TimeUnit.MILLISECONDS

    fun stop() {
        job.cancel()
        singleThreadExecutor.shutdown()
    }

    fun start() = CoroutineScope(Dispatchers.IO).launch {
        delay(targetUnit.convert(initialDelay, timeUnit))
        while (isActive) {
            action(this)
            delay(targetUnit.convert(interval, timeUnit))
        }
    }
}
