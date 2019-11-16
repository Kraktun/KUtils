package com.kraktun.kutils.coroutines.optimizators

import java.util.*

/**
 * Simple optimizer that chooses what a coroutine should do:
 *      If the number of coroutine executing the highFunction equals highThreads
 *      Then next coroutine will execute lowFunction
 *      Otherwise it will execute highFunction
 * This allows to have a fixed number of coroutines executing an intensive function (highFunction), while others a slower but less intensive function (lowFunction).
 * Both highFunction and lowFunction must return the same result for the same input value.
 * @param files list of objects. The list must be ordered by priority, where [0] is highest.
 * @param highThreads number of coroutines to use to execute highFunction
 * @param validator function that returns if a file can be executed in highFunction
 * @param highFunction function to execute on high priority
 * @param lowFunction function to execute on low priority
 */
class NumberedOptimizer<K, P> (
    private val files: List<K>,
    private val highThreads: Int = 1,
    private val validator: (K) -> Boolean = {true},
    private val highFunction: (K) -> P,
    private val lowFunction: (K) -> P) : Optimizer<K, P> {

    private val elements = LinkedList(files)
    private var freeHighs = highThreads

    override fun getSize(): Int {
        return files.size
    }

    override fun executeNext(): Pair<K, P> {
        var isHigh : Boolean
        val el : K
        val result : P
        synchronized(this) {
            isHigh = freeHighs > 0 && validator(elements.first)
            el = if (isHigh) {
                freeHighs--
                elements.removeFirst()
            } else {
                elements.removeLast()
            }
        }
        if (isHigh) {
            result = highFunction(el)
            synchronized(this) {
                freeHighs++
            }
        } else {
            result = lowFunction(el)
        }
        return Pair(el, result)
    }
}