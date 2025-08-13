package io.coursepick.coursepick.domain

import io.coursepick.coursepick.domain.course.Latitude
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LatitudeTest {
    @ParameterizedTest
    @ValueSource(doubles = [-91.0, 91.0])
    fun `위도는 -90 이상 90 이하이다`(value: Double) {
        assertThrows<IllegalArgumentException> { Latitude(value) }
    }
}
