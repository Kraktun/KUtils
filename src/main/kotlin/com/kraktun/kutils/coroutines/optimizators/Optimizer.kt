package com.kraktun.kutils.coroutines.optimizators

interface Optimizer {

    /**
     * The result must always be a pair, with the input value as the first element
     */
    fun executeNext() : Any

    /**
     * Number of objects to process. This is the total length (not the current length).
     */
    fun getSize() : Int
}