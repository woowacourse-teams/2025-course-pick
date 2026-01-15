package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import io.coursepick.coursepick.domain.customcourse.DraftCourse
import kotlinx.serialization.Serializable

@Serializable
data class DraftCourseDto(
    val name: String,
    val coordinates: List<CoordinateDto>,
) {
    companion object {
        operator fun invoke(draftCourse: DraftCourse): DraftCourseDto =
            DraftCourseDto(
                name = draftCourse.name.value,
                coordinates = draftCourse.coordinates.map(CoordinateDto::invoke),
            )
    }
}
