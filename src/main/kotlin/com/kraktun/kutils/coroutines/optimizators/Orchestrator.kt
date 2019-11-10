package com.kraktun.kutils.coroutines.optimizators

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.ConcurrentHashMap

/**
 * Instantiate the coroutines and assign functions according to the chosen optimizer.
 */
class Orchestrator {

    /**
     * @param optimizer optimizer
     * @param functionK function to execute on each element of the list passed to the optimizer, to get the key for the map
     * @param channelCapacity capacity of the channel
     * @param threads number of coroutines to start
     * @return map with functionK as key and function passed to the optimizer as value
     */
    @Suppress("UNCHECKED_CAST")
    fun<T, P, K> run(optimizer: Optimizer, functionK: (T) -> P, channelCapacity: Int, threads: Int): Map<P, K> {
        val newMap = ConcurrentHashMap<P, K>()
        runBlocking {
            val counter = IntArray(optimizer.getSize()) {it + 1}
            val listChannel = Channel<Int>(capacity = channelCapacity)
            launch {
                counter.forEach {
                    listChannel.send(it)
                }
                listChannel.close()
            }
            val waitingFor = mutableSetOf<Deferred<Unit>>()
            for (t in 1..threads) {
                waitingFor.add(GlobalScope.async(CoroutineName("Core$t")) {
                    for (f in listChannel) {
                        println("Processing element $f/${optimizer.getSize()}")
                        val result = optimizer.executeNext() as Pair<T, K>
                        newMap[functionK(result.first)] = result.second
                    }
                })
            }
            waitingFor.map { it.await()}
            delay(100)
        }
        return newMap
    }
}