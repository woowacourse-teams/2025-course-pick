package io.coursepick.coursepick.presentation

import android.content.Context
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Kilometer
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Meter

fun Length.toDistanceText(): String =
    if (meter.value < 1000) {
        "%.0f m".format(meter.value)
    } else {
        "%.2f km".format(meter.value / 1000)
    }

fun formattedMeter(
    context: Context,
    meter: Meter,
): String =
    if (meter < Kilometer.METRIC_MULTIPLIER) {
        context.getString(R.string.course_item_unit_meter, meter.value.toInt())
    } else {
        context.getString(R.string.course_item_unit_kilometer, meter.toKilometer().value)
    }
