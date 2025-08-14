package io.coursepick.coursepick.domain.course

enum class InclineSummary {
    FLAT,
    REPEATING_HILLS,
    SOMETIMES_UPHILL,
    SOMETIMES_DOWNHILL,
    CONTINUOUS_UPHILL,
    CONTINUOUS_DOWNHILL,
    UNKNOWN,
    ;

    companion object {
        operator fun invoke(value: String): InclineSummary =
            InclineSummary.entries.firstOrNull { inclineSummary: InclineSummary ->
                inclineSummary.name == value
            } ?: UNKNOWN

        fun random() = entries.random()
    }
}
