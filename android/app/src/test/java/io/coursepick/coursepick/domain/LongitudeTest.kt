package io.coursepick.coursepick.domain

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LongitudeTest {
    @ParameterizedTest
    @ValueSource(doubles = [-181.0, 180.0])
    fun `경도는 -180 이상 180 미만이다`(value: Double) {
        assertThrows<IllegalArgumentException> { Longitude(value) }
    }
}
