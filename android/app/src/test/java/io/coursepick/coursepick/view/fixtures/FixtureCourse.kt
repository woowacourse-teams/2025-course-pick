package io.coursepick.coursepick.view.fixtures

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.domain.CourseDifficulty
import io.coursepick.coursepick.domain.CourseName
import io.coursepick.coursepick.domain.Distance
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Length
import io.coursepick.coursepick.domain.Longitude

fun fakeCourse(
    id: Long,
    name: String,
    distance: Int,
    length: Int,
    latitude: Double,
    longitude: Double,
    type: String? = null,
    difficulty: CourseDifficulty = CourseDifficulty.UNKNOWN,
): Course =
    Course(
        id = id,
        name = CourseName(name),
        distance = Distance(distance),
        length = Length(length),
        coordinates = listOf(Coordinate(Latitude(latitude), Longitude(longitude))),
        type = type,
        difficulty = difficulty,
    )

val COURSE_1 = fakeCourse(1, "코스 1", 10, 100, 1.0, 1.0)
val COURSE_2 = fakeCourse(2, "코스 2", 20, 200, 2.0, 2.0)
val COURSE_3 = fakeCourse(3, "코스 3", 30, 300, 3.0, 3.0)
val COURSE_4 = fakeCourse(4, "코스 4", 40, 400, 4.0, 4.0)
val COURSE_5 = fakeCourse(5, "코스 5", 50, 500, 5.0, 5.0)
val COURSE_6 = fakeCourse(6, "코스 6", 60, 600, 6.0, 6.0)
val COURSE_7 = fakeCourse(7, "코스 7", 70, 700, 7.0, 7.0)
val COURSE_8 = fakeCourse(8, "코스 8", 80, 800, 8.0, 8.0)
val COURSE_9 = fakeCourse(9, "코스 9", 90, 900, 9.0, 9.0)
val COURSE_10 = fakeCourse(10, "코스 10", 100, 1000, 10.0, 10.0)
val COURSE_11 = fakeCourse(11, "코스 11", 110, 1100, 11.0, 11.0)
val COURSE_12 = fakeCourse(12, "코스 12", 120, 1200, 12.0, 12.0)
val COURSE_13 = fakeCourse(13, "코스 13", 130, 1300, 13.0, 13.0)
val COURSE_14 = fakeCourse(14, "코스 14", 140, 1400, 14.0, 14.0)
val COURSE_15 = fakeCourse(15, "코스 15", 150, 1500, 15.0, 15.0)
val COURSE_16 = fakeCourse(16, "코스 16", 160, 1600, 16.0, 16.0)
val COURSE_17 = fakeCourse(17, "코스 17", 170, 1700, 17.0, 17.0)
val COURSE_18 = fakeCourse(18, "코스 18", 180, 1800, 18.0, 18.0)
val COURSE_19 = fakeCourse(19, "코스 19", 190, 1900, 19.0, 19.0)
val COURSE_20 = fakeCourse(20, "코스 20", 200, 2000, 20.0, 20.0)

val FAKE_COURSES: List<Course> =
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
