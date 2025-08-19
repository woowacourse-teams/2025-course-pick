package io.coursepick.coursepick.domain.fixture

import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.InclineType
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.course.Segment

val COURSE_FIXTURE_1 = Course("1", "코스 1", 10, 100, 1.0, 1.0)
val COURSE_FIXTURE_2 = Course("2", "코스 2", 20, 200, 2.0, 2.0)
val COURSE_FIXTURE_3 = Course("3", "코스 3", 30, 300, 3.0, 3.0)
val COURSE_FIXTURE_4 = Course("4", "코스 4", 40, 400, 4.0, 4.0)
val COURSE_FIXTURE_5 = Course("5", "코스 5", 50, 500, 5.0, 5.0)
val COURSE_FIXTURE_6 = Course("6", "코스 6", 60, 600, 6.0, 6.0)
val COURSE_FIXTURE_7 = Course("7", "코스 7", 70, 700, 7.0, 7.0)
val COURSE_FIXTURE_8 = Course("8", "코스 8", 80, 800, 8.0, 8.0)
val COURSE_FIXTURE_9 = Course("9", "코스 9", 90, 900, 9.0, 9.0)
val COURSE_FIXTURE_10 = Course("10", "코스 10", 100, 1000, 10.0, 10.0)
val COURSE_FIXTURE_11 = Course("11", "코스 11", 110, 1100, 11.0, 11.0)
val COURSE_FIXTURE_12 = Course("12", "코스 12", 120, 1200, 12.0, 12.0)
val COURSE_FIXTURE_13 = Course("13", "코스 13", 130, 1300, 13.0, 13.0)
val COURSE_FIXTURE_14 = Course("14", "코스 14", 140, 1400, 14.0, 14.0)
val COURSE_FIXTURE_15 = Course("15", "코스 15", 150, 1500, 15.0, 15.0)
val COURSE_FIXTURE_16 = Course("16", "코스 16", 160, 1600, 16.0, 16.0)
val COURSE_FIXTURE_17 = Course("17", "코스 17", 170, 1700, 17.0, 17.0)
val COURSE_FIXTURE_18 = Course("18", "코스 18", 180, 1800, 18.0, 18.0)
val COURSE_FIXTURE_19 = Course("19", "코스 19", 190, 1900, 19.0, 19.0)
val COURSE_FIXTURE_20 = Course("20", "코스 20", 200, 2000, 20.0, 20.0)

val FAKE_COURSES: List<Course> =
    listOf(
        COURSE_FIXTURE_1,
        COURSE_FIXTURE_2,
        COURSE_FIXTURE_3,
        COURSE_FIXTURE_4,
        COURSE_FIXTURE_5,
        COURSE_FIXTURE_6,
        COURSE_FIXTURE_7,
        COURSE_FIXTURE_8,
        COURSE_FIXTURE_9,
        COURSE_FIXTURE_10,
        COURSE_FIXTURE_11,
        COURSE_FIXTURE_12,
        COURSE_FIXTURE_13,
        COURSE_FIXTURE_14,
        COURSE_FIXTURE_15,
        COURSE_FIXTURE_16,
        COURSE_FIXTURE_17,
        COURSE_FIXTURE_18,
        COURSE_FIXTURE_19,
        COURSE_FIXTURE_20,
    )

private fun Course(
    id: String,
    name: String,
    distance: Int,
    length: Int,
    latitude: Double,
    longitude: Double,
    type: String = "트랙",
    difficulty: String = "쉬움",
): Course =
    Course(
        id = id,
        name = CourseName(name),
        distance = Distance(distance),
        length = Length(length),
        roadType = type,
        difficulty = difficulty,
        segments =
            listOf(
                Segment(
                    InclineType.UNKNOWN,
                    listOf(
                        Coordinate(Latitude(latitude), Longitude(longitude)),
                        Coordinate(Latitude(latitude + 0.0001), Longitude(longitude + 0.0001)),
                    ),
                ),
            ),
    )
