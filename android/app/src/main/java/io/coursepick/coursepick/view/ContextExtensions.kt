package io.coursepick.coursepick.view

import android.content.Context

fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density
