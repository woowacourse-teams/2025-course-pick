package io.coursepick.coursepick.domain

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LatitudeTest {
    @ParameterizedTest
    @ValueSource(ints = [-91, 91])
    fun `위도는 -90 이상 90 이하이다`(value: Int) {
        assertThrows<IllegalArgumentException> { Latitude(value) }
    }
}
