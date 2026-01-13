package io.coursepick.coursepick.data.customcourse

import io.coursepick.coursepick.data.course.CoordinateDto
import io.coursepick.coursepick.domain.customcourse.DraftCourse
import io.coursepick.coursepick.domain.customcourse.RoadType
import kotlinx.serialization.Serializable

@Serializable
data class DraftCourseDto(
    val name: String,
    val roadType: String,
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
                coordinates = draftCourse.coordinates.map(CoordinateDto::invoke),
            )
    }
}
