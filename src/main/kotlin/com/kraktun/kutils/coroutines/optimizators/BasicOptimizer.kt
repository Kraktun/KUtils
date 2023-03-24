package com.kraktun.kutils.coroutines.optimizators

import java.util.*

/**
 * Simple optimizer that executes a function.
 * @param files list of objects. The list is parsed as FIFO.
 * @param function function to execute
 */
class BasicOptimizer<K, P> (
    private val files: List<K>,
    private val function: (K) -> P,
) : Optimizer<K, P> {

    private val elements = LinkedList(files)

    override fun getSize(): Int {
        return files.size
    }

    override fun executeNext(): Pair<K, P> {
        val el: K
        synchronized(this) {
            el = elements.removeFirst()
        }
        val result: P = function(el)
        return Pair(el, result)
    }
}
