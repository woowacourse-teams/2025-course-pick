package io.coursepick.coursepick.domain

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SegmentTest {
    @ParameterizedTest()
    @ValueSource(ints = [0, 1])
    fun `세그먼트는 두 개 이상의 좌표로 이루어져 있다`(size: Int) {
        // given
        val inclineType = InclineType.UNKNOWN
        val coordinates: List<Coordinate> =
            List(size) {
                Coordinate(Latitude(37.5642135), Longitude(127.0016985))
            }
        // when & then
        assertThrows<IllegalArgumentException> { Segment(inclineType, coordinates) }
    }
}
