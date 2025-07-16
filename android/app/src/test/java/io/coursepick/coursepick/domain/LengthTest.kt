package io.coursepick.coursepick.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LengthTest {
    @Test
    fun `길이는 0 이상이다`() {
        assertThrows<IllegalArgumentException> { Length(-1) }
    }
}
