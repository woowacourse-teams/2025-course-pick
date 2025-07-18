package io.coursepick.coursepick.view

import android.content.Context
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class DoubleBackPressHandler(
    private val context: Context,
    private val intervalTime: Long = LIMIT_TIME,
    private val toastMessage: String,
) {
    private var backPressedTime: Long = 0

    fun handleBackPress(onExit: () -> Unit): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - backPressedTime >= intervalTime) {
            backPressedTime = currentTime
            showToast()
            false
        } else {
            onExit()
            true
        }
    }

    private fun showToast() {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }

    fun setupWith(activity: AppCompatActivity) {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress { activity.finish() }
                }
            }
        activity.onBackPressedDispatcher.addCallback(activity, callback)
    }

    companion object {
        private const val LIMIT_TIME = 2000L
    }
}
