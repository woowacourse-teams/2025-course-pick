package io.coursepick.coursepick.view.fixtures

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Length
import io.coursepick.coursepick.domain.Longitude

fun createCourse(
    id: Long,
    name: String,
    distance: Int,
    length: Int,
    lat: Double,
    lon: Double,
): Course =
    Course(
        id = id,
        name = CourseName(name),
        distance = Distance(distance),
        length = Length(length),
        coordinates = listOf(Coordinate(Latitude(lat), Longitude(lon))),
    )

val COURSE_1 = createCourse(1, "코스 1", 10, 100, 1.0, 1.0)
val COURSE_2 = createCourse(2, "코스 2", 20, 200, 2.0, 2.0)
val COURSE_3 = createCourse(3, "코스 3", 30, 300, 3.0, 3.0)
val COURSE_4 = createCourse(4, "코스 4", 40, 400, 4.0, 4.0)
val COURSE_5 = createCourse(5, "코스 5", 50, 500, 5.0, 5.0)
val COURSE_6 = createCourse(6, "코스 6", 60, 600, 6.0, 6.0)
val COURSE_7 = createCourse(7, "코스 7", 70, 700, 7.0, 7.0)
val COURSE_8 = createCourse(8, "코스 8", 80, 800, 8.0, 8.0)
val COURSE_9 = createCourse(9, "코스 9", 90, 900, 9.0, 9.0)
val COURSE_10 = createCourse(10, "코스 10", 100, 1000, 10.0, 10.0)
val COURSE_11 = createCourse(11, "코스 11", 110, 1100, 11.0, 11.0)
val COURSE_12 = createCourse(12, "코스 12", 120, 1200, 12.0, 12.0)
val COURSE_13 = createCourse(13, "코스 13", 130, 1300, 13.0, 13.0)
val COURSE_14 = createCourse(14, "코스 14", 140, 1400, 14.0, 14.0)
val COURSE_15 = createCourse(15, "코스 15", 150, 1500, 15.0, 15.0)
val COURSE_16 = createCourse(16, "코스 16", 160, 1600, 16.0, 16.0)
val COURSE_17 = createCourse(17, "코스 17", 170, 1700, 17.0, 17.0)
val COURSE_18 = createCourse(18, "코스 18", 180, 1800, 18.0, 18.0)
val COURSE_19 = createCourse(19, "코스 19", 190, 1900, 19.0, 19.0)
val COURSE_20 = createCourse(20, "코스 20", 200, 2000, 20.0, 20.0)

val MOCK_COURSES: List<Course> =
    listOf(
        COURSE_1,
        COURSE_2,
        COURSE_3,
        COURSE_4,
        COURSE_5,
        COURSE_6,
        COURSE_7,
        COURSE_8,
        COURSE_9,
        COURSE_10,
        COURSE_11,
        COURSE_12,
        COURSE_13,
        COURSE_14,
        COURSE_15,
        COURSE_16,
        COURSE_17,
        COURSE_18,
        COURSE_19,
        COURSE_20,
    )
