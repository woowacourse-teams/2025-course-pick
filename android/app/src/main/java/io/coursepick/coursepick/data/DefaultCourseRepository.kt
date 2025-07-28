package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseDifficulty
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.CourseRepository
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.InclineType
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Length
import io.coursepick.coursepick.domain.Longitude
import io.coursepick.coursepick.domain.Segment

class DefaultCourseRepository : CourseRepository {
//    override suspend fun courses(
//        latitude: Latitude,
//        longitude: Longitude,
//    ): List<Course> =
//        Services.courseService
//            .courses(latitude.value, longitude.value)
//            .mapNotNull { item: CourseDto ->
//                item.toCourseOrNull()
//            }

    override suspend fun courses(
        latitude: Latitude,
        longitude: Longitude,
    ): List<Course> =
        listOf(
            Course(
                id = 1L,
                name = CourseName("더미 코스"),
                distance = Distance(5000),
                length = Length(5000),
                roadType = "아스팔트",
                difficulty = CourseDifficulty.EASY,
                segments =
                    listOf(
                        Segment(
                            inclineType = InclineType.FLAT,
                            coordinates =
                                listOf(
                                    Coordinate(Latitude(37.5665), Longitude(126.9780)),
                                    Coordinate(Latitude(37.5670), Longitude(126.9790)),
                                    Coordinate(Latitude(37.5512), Longitude(126.9882)),
                                ),
                        ),
                        Segment(
                            inclineType = InclineType.UPHILL,
                            coordinates =
                                listOf(
                                    Coordinate(Latitude(37.5512), Longitude(126.9882)),
                                    Coordinate(Latitude(37.5525), Longitude(126.9900)),
                                    Coordinate(Latitude(37.6060), Longitude(126.9700)),
                                ),
                        ),
                        Segment(
                            inclineType = InclineType.DOWNHILL,
                            coordinates =
                                listOf(
                                    Coordinate(Latitude(37.6060), Longitude(126.9700)),
                                    Coordinate(Latitude(37.6050), Longitude(126.9680)),
                                    Coordinate(Latitude(37.5665), Longitude(126.9780)),
                                ),
                        ),
                    ),
            ),
        )
}
