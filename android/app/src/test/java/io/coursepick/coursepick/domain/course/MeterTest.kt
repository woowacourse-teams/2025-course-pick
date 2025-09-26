package io.coursepick.coursepick.domain.course

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MeterTest {
    @Test
    fun `Meter는 0 이상이다`() {
        assertThrows<IllegalArgumentException> { Meter(-1) }
    }
}
