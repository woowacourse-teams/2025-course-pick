package io.coursepick.coursepick.presentation.filter

interface FilterAction {
    fun cancel()

    fun reset()

    fun apply()
}
