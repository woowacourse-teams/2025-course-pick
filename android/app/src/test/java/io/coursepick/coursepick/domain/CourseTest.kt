package io.coursepick.coursepick.domain

import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Length
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CourseTest {
    @Test
    fun `코스는 한 개 이상의 세그먼트로 이루어져 있다`() {
        assertThrows<IllegalArgumentException> {
            Course(
                0L,
                CourseName("석촌호수 러닝 코스"),
                Distance(300),
                Length(3000),
                "트랙",
                "쉬움",
                emptyList(),
            )
        }
    }
}
