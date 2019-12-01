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
    } else default
}

/**
 * Reduce a list to a string with each element of the string in a new line
 */
fun List<*>.toBasicString() : String {
    return this.map{ it.toString() }.reduce { acc, s ->
        if (acc.isEmpty())
            s
        else
            "$acc\n$s"
    }
}