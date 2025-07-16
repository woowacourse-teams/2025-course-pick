package io.coursepick.coursepick.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CourseTest {
    @Test
    fun `코스는 한 개 이상의 좌표를 갖는다`() {
        assertThrows<IllegalArgumentException> {
            Course(
                0L,
                CourseName("석촌호수 러닝 코스"),
                Distance(300),
                Length(3000),
                emptyList()
            )
        }
    }
}
