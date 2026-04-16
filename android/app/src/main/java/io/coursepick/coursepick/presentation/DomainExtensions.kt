package io.coursepick.coursepick.presentation

import io.coursepick.coursepick.domain.course.Length

fun Length.toDistanceText(): String =
    if (meter.value < 1000) {
        "%.0f m".format(meter.value)
    } else {
        "%.2f km".format(meter.value / 1000)
    }
