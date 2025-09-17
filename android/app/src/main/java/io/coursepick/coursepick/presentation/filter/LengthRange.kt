package io.coursepick.coursepick.presentation.filter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LengthRange(
    val minimum: Int,
    val maximum: Int,
) : Parcelable
