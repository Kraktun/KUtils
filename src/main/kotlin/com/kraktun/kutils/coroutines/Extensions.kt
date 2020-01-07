package com.kraktun.kutils.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Execute mapIndexed() in parallel on a collection
 */
suspend fun <A, B> Iterable<A>.parallelMapIndexed(f: suspend (Int,A) -> B): List<B> = coroutineScope {
    mapIndexed { ind, obj ->
        GlobalScope.async {
            f(ind,obj)
        }
    }.awaitAll()
}