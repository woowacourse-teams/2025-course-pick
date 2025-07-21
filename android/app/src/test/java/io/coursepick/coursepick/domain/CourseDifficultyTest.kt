package io.coursepick.coursepick.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CourseDifficultyTest {
    @ValueSource(doubles = [0.1, 1.1, 2.2, 2.9])
    @ParameterizedTest
    fun `난이도는 도로일 때, 점수가 4점 미만이면 EASY이다`(value: Double) {
        // given
        val type = CourseType.ROAD
        val score = CourseScore(value)
        val expected = CourseDifficulty.EASY

        // when
        val actual = CourseDifficulty.courseDifficulty(type, score)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @ValueSource(doubles = [4.0, 5.9])
    @ParameterizedTest
    fun `난이도는 도로일 때, 점수가 7점 미만이면 NORMAL이다`(value: Double) {
        // given
        val type = CourseType.ROAD
        val score = CourseScore(value)
        val expected = CourseDifficulty.NORMAL

        // when
        val actual = CourseDifficulty.courseDifficulty(type, score)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @ValueSource(doubles = [6.0, 9.9])
    @ParameterizedTest
    fun `난이도는 도로일 때, 점수가 10점 미만이면 HARD이다`(value: Double) {
        // given
        val type = CourseType.ROAD
        val score = CourseScore(value)
        val expected = CourseDifficulty.HARD

        // when
        val actual = CourseDifficulty.courseDifficulty(type, score)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @ValueSource(doubles = [0.0, 10.0])
    @ParameterizedTest
    fun `난이도는 도로일 때, 점수가 0점 이하 10점 이상이면 에러를 발생시킨다`(value: Double) {
        assertThrows<IllegalArgumentException> {
            CourseDifficulty.courseDifficulty(
                CourseType.TRACK,
                CourseScore(value),
            )
        }
    }

    @ValueSource(doubles = [0.1, 3.9])
    @ParameterizedTest
    fun `난이도는 트랙일 때, 점수가 4점 미만이면 EASY이다`(value: Double) {
        // given
        val type = CourseType.TRACK
        val score = CourseScore(value)
        val expected = CourseDifficulty.EASY

        // when
        val actual = CourseDifficulty.courseDifficulty(type, score)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @ValueSource(doubles = [0.1, 1.9])
    @ParameterizedTest
    fun `난이도는 산길일 때, 점수가 2점 미만이면 EASY이다`(value: Double) {
        // given
        val type = CourseType.ROAD
        val score = CourseScore(value)
        val expected = CourseDifficulty.EASY

        // when
        val actual = CourseDifficulty.courseDifficulty(type, score)

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
