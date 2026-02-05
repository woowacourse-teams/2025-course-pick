package io.coursepick.coursepick.presentation.model

import io.coursepick.coursepick.domain.course.Meter

val Meter.formatted: String
    get() =
        if (this < 1000) {
            "${this}m"
        } else {
            String.format(
                locale = null,
                format = "%.2f",
                this.toKilometer(),
            ) + "km"
        }
