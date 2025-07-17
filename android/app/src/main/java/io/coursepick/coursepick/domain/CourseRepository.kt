package io.coursepick.coursepick.domain

interface CourseRepository {
    val courses: List<Course>
}

class DefaultCourseRepository : CourseRepository {
    override val courses: List<Course>
        get() =
            List(20) { index: Int ->
                Course(
                    index.toLong(),
                    CourseName("코스 $index"),
                    Distance(index * 10),
                    Length(index * 100),
                    listOf(
                        Coordinate(
                            Latitude(index.toDouble()),
                            Longitude(index.toDouble()),
                        ),
                    ),
                )
            }
}
