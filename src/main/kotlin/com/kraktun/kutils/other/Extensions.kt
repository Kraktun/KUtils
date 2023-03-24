package com.kraktun.kutils.other

import java.util.concurrent.locks.ReadWriteLock

inline fun <K> ReadWriteLock?.writeInLock(f: () -> K): K {
    this?.writeLock()?.lock()
    try {
        return f.invoke()
    } finally {
        this?.writeLock()?.unlock()
    }
}

inline fun <K> ReadWriteLock?.readInLock(f: () -> K): K {
    this?.readLock()?.lock()
    try {
        return f.invoke()
    } finally {
        this?.readLock()?.unlock()
    }
}
