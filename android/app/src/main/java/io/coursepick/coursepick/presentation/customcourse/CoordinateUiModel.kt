package io.coursepick.coursepick.presentation.customcourse

import android.os.Parcelable
import io.coursepick.coursepick.domain.course.Coordinate
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoordinateUiModel(
    val value: Coordinate,
) : Parcelable
