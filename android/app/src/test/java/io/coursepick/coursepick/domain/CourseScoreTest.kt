package io.coursepick.coursepick.domain

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CourseScoreTest {
    @ValueSource(doubles = [0.0, 10.0])
    @ParameterizedTest
    fun `코스 점수가 0점 이하, 10점 이상이면 IllegalArgumentException이 발생한다`(value: Double) {
        assertThrows<IllegalArgumentException> { CourseScore(value) }
    }
}
