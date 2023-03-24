package com.kraktun.kutils.collections

/**
 * Execute function for a collection, defaults to passed value if list is empty or null, or an exception is thrown.
 */
inline fun <E : Any, T : Collection<E>> T?.ifNotEmpty(func: T.() -> Any?, default: Any?): Any? {
    return if (this != null && this.isNotEmpty()) {
        try {
            func(this)
        } catch (e: Exception) {
            default
        }
    } else {
        default
    }
}

/**
 * Reduce a collection to a string with each element of the collection in a new line
 */
fun Collection<*>.toBasicString(): String {
    return buildString { this@toBasicString.forEach { if (this.isNotEmpty()) this.append("\n$it") else this.append(it) } }
}

/**
 * Reduce a map to a string with each element of the map in a new line
 */
fun Map<*, *>.toBasicString(): String {
    return buildString { this@toBasicString.forEach { if (this.isNotEmpty()) this.append("\n$it") else this.append(it) } }
}

/**
 * Simple and easy way to replace elements in a collections
 */
fun <E> Iterable<E>.replaceAll(old: E, new: E) = map { if (it == old) new else it }
