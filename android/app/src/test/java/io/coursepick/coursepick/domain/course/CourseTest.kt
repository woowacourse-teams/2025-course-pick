package io.coursepick.coursepick.domain.course

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CourseTest {
    @Test
    fun `코스는 두 개 이상의 좌표로 이루어져 있다`() {
        assertThrows<IllegalArgumentException> {
            Course(
                "",
                CourseName("석촌호수 러닝 코스"),
                Distance(300),
                Length(3000),
                emptyList(),
            )
        }
    }
}
