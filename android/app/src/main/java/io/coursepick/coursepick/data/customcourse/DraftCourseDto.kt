package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import io.coursepick.coursepick.domain.customcourse.Difficulty
import io.coursepick.coursepick.domain.customcourse.DraftCourse
import io.coursepick.coursepick.domain.customcourse.RoadType

data class DraftCourseDto(
    val name: String,
    val roadType: String,
    val difficulty: String,
    val coordinates: List<CoordinateDto>,
) {
    companion object {
        operator fun invoke(draftCourse: DraftCourse): DraftCourseDto =
            DraftCourseDto(
                name = draftCourse.name.value,
                roadType =
                    when (draftCourse.roadType) {
                        RoadType.TRACK -> "트랙"
                        RoadType.TRAIL -> "트레일"
                        RoadType.SIDEWALK -> "보도"
                        RoadType.UNKNOWN -> "알수없음"
                    },
                difficulty =
                    when (draftCourse.difficulty) {
                        Difficulty.EASY -> "쉬움"
                        Difficulty.NORMAL -> "보통"
                        Difficulty.HARD -> "어려움"
                        Difficulty.UNKNOWN -> "알수없음"
                    },
                coordinates = draftCourse.coordinates.map(CoordinateDto::invoke),
            )
    }
}
