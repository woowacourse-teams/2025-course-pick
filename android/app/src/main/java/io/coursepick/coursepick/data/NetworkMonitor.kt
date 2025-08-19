package io.coursepick.coursepick.data

fun interface NetworkMonitor {
    fun isConnected(): Boolean
}
