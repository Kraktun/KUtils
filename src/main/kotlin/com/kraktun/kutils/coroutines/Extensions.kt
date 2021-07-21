package com.kraktun.kutils.coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Execute mapIndexed() in parallel on a collection
 */
suspend fun <A, B> Iterable<A>.parallelMapIndexed(f: suspend (Int, A) -> B): List<B> = coroutineScope {
    mapIndexed { ind, obj ->
        async {
            f(ind,obj)
        }
    }.awaitAll()
}

/**
 * Execute mapIndexed() in chunkSize parallel threads on a collection
 */
suspend fun <A, B> Iterable<A>.parallelMapIndexedChunked(chunkSize: Int, f: suspend (Int, A) -> B): List<B> = coroutineScope {
    chunked(chunkSize).map { chunk ->
        async {
            chunk.mapIndexed { index, obj ->
                f(index, obj)
            }
        }
    }.awaitAll().flatten()
}