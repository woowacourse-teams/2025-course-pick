package io.coursepick.coursepick.view

class DoubleBackPressDetector(
    private val intervalTime: Long = 2000L,
) {
    private var lastPressedTime: Long = 0

    fun doubleBackPressed(): Boolean {
        val now: Long = System.currentTimeMillis()
        val result: Boolean = now - lastPressedTime < intervalTime
        lastPressedTime = now
        return result
    }
}
