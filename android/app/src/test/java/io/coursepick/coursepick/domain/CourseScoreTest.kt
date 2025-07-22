package io.coursepick.coursepick.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CourseScoreTest {
    @ValueSource(doubles = [0.0, 10.0])
    @ParameterizedTest
    fun `코스 점수가 0점 이하, 10점 이상이면 IllegalArgumentException이 발생한다`(value: Double) {
        assertThrows<IllegalArgumentException> { CourseScore(value) }
    }

    @ValueSource(doubles = [0.1, 2.9])
    @ParameterizedTest
    fun `난이도는 점수가 3점 미만이면 EASY이다`(value: Double) {
        // given
        val score = CourseScore(value)
        val expected = CourseDifficulty.EASY

        // when
        val actual = score.courseDifficulty()

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @ValueSource(doubles = [3.0, 6.9])
    @ParameterizedTest
    fun `난이도는 점수가 7점 미만이면 NORMAL이다`(value: Double) {
        // given
        val score = CourseScore(value)
        val expected = CourseDifficulty.NORMAL

        // when
        val actual = score.courseDifficulty()

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @ValueSource(doubles = [7.0, 9.9])
    @ParameterizedTest
    fun `난이도는 점수가 10점 미만이면 HARD이다`(value: Double) {
        // given
        val score = CourseScore(value)
        val expected = CourseDifficulty.HARD

        // when
        val actual = score.courseDifficulty()

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
