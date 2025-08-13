package io.coursepick.coursepick.domain

import io.coursepick.coursepick.domain.course.CourseName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CourseNameTest {
    @ParameterizedTest
    @ValueSource(strings = ["0", "0123456789012345678901234567890"])
    fun `코스 이름은 2자 이상 30자 이하이다`(value: String) {
        assertThrows<IllegalArgumentException> { CourseName(value) }
    }
}
