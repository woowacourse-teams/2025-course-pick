package io.coursepick.coursepick.presentation.model

enum class Difficulty(
    val label: String,
) {
    EASY("쉬움"),
    NORMAL("보통"),
    HARD("어려움"),
    UNKNOWN("알 수 없음"),
    ;

    companion object {
        fun from(value: String): Difficulty =
            when (value) {
                "쉬움" -> EASY
                "보통" -> NORMAL
                "어려움" -> HARD
                else -> UNKNOWN
            }
    }
}
