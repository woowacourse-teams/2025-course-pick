package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import io.coursepick.coursepick.domain.customcourse.DraftCourse

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
                roadType = draftCourse.roadType,
                difficulty = draftCourse.difficulty,
                coordinates = draftCourse.coordinates.map(CoordinateDto::invoke),
            )
    }
}
