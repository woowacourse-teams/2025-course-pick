package io.coursepick.coursepick.presentation.createcustomcourse

import android.os.Parcelable
import io.coursepick.coursepick.domain.course.Coordinate
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoordinateUiModel(
    val latitude: Double,
    val longitude: Double,
) : Parcelable

fun Coordinate.toUiModel(): CoordinateUiModel = CoordinateUiModel(latitude.value, longitude.value)
