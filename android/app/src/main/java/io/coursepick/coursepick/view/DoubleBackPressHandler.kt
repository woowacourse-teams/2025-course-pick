package io.coursepick.coursepick.view

import android.content.Context
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class DoubleBackPressHandler(
    private val toastMessage: String,
    private val intervalTime: Long = LIMIT_TIME,
) {
    private var backPressedTime: Long = 0

    fun handleBackPress(
        context: Context,
        onExit: () -> Unit,
    ): Boolean {
        val currentTime: Long = System.currentTimeMillis()
        return if (currentTime - backPressedTime >= intervalTime) {
            backPressedTime = currentTime
            showToast(context)
            false
        } else {
            onExit()
            true
        }
    }

    fun setUpWith(activity: AppCompatActivity) {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress(activity.baseContext) { activity.finish() }
                }
            }
        activity.onBackPressedDispatcher.addCallback(activity, callback)
    }

    private fun showToast(context: Context) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val LIMIT_TIME = 2000L
    }
}
