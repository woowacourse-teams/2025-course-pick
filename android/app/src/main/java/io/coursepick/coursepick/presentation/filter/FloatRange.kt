package io.coursepick.coursepick.presentation.filter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FloatRange(
    val first: Float,
    val last: Float,
) : Parcelable {
    val toList: List<Float> get() = listOf(first, last)
}
