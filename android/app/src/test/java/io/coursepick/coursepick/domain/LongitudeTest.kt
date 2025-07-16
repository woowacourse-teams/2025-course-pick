package io.coursepick.coursepick.domain

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LongitudeTest {
    @ParameterizedTest
    @ValueSource(ints = [-181, 180])
    fun `경도는 -180 이상 180 미만이다`(value: Int) {
        assertThrows<IllegalArgumentException> { Longitude(value) }
    }
}
