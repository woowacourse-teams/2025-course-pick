package io.coursepick.coursepick.presentation.filter

import io.coursepick.coursepick.domain.course.Kilometer
import io.coursepick.coursepick.domain.course.Meter
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.course.CourseListItem

data class CourseFilter(
    val lengthRange: ClosedRange<Kilometer>,
) {
    val lengthRangeAsFloat: ClosedFloatingPointRange<Float> =
        lengthRange.start.value.toFloat()..lengthRange.endInclusive.value.toFloat()

    private val minimumLength: Meter = lengthRange.start.toMeter()
    private val maximumLength: Meter =
        if (lengthRange.endInclusive == MAXIMUM_LENGTH_RANGE) {
            Meter.MAX_VALUE
        } else {
            lengthRange.endInclusive.toMeter()
        }

    fun filteredCourses(courses: List<CourseListItem>): List<CourseListItem> =
        courses.filter { courseListItem: CourseListItem ->
            courseListItem is CourseListItem.Loading ||
                (
                    courseListItem is CourseListItem.Course &&
                        this.matches(
                            courseListItem.item,
                        )
                )
        }

    fun matches(courseItem: CourseItem): Boolean = Meter(courseItem.length) in minimumLength..maximumLength

    companion object {
        val MINIMUM_LENGTH_RANGE = Kilometer(0.0)
        val MAXIMUM_LENGTH_RANGE = Kilometer(21.0)
        val None =
            CourseFilter(
                lengthRange = MINIMUM_LENGTH_RANGE..MAXIMUM_LENGTH_RANGE,
            )
    }
}
