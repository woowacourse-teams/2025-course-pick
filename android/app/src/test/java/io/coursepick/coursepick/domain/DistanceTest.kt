package io.coursepick.coursepick.domain

import io.coursepick.coursepick.domain.course.Distance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DistanceTest {
    @Test
    fun `거리는 0 이상이다`() {
        assertThrows<IllegalArgumentException> { Distance(-1) }
    }
}
