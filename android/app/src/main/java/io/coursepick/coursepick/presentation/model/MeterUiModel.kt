package io.coursepick.coursepick.presentation.model

import io.coursepick.coursepick.domain.course.Meter

@JvmInline
value class MeterUiModel(
    val value: String,
)

fun Meter.toUiModel(): MeterUiModel =
    MeterUiModel(
        if (this < 1000) {
            "${value.toInt()}m"
        } else {
            String.format(
                locale = null,
                format = "%.2f",
                toKilometer().value,
            ) + "km"
        },
    )
