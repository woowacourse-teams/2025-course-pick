package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.Course
import io.coursepick.coursepick.view.fixtures.COURSE_20
import io.coursepick.coursepick.view.fixtures.FAKE_COURSES
import io.coursepick.coursepick.view.fixtures.FakeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class MainViewModelTest {
    private val fakeRepository = FakeRepository(FAKE_COURSES)
    private lateinit var mainViewModel: MainViewModel

    @Test
    fun `초기 상태에서는 가장 가까운 코스가 선택된다`() {
        // given
        mainViewModel = MainViewModel(fakeRepository)
        val expected =
            MainUiState(
                FAKE_COURSES.mapIndexed { index: Int, course: Course ->
                    CourseItem(course, selected = index == 0)
                },
            )
        val actual = mainViewModel.state.getOrAwaitValue()

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `하나의 코스를 선택하면 나머지는 해제되고 해당 코스만 선택된다`() {
        // given
        mainViewModel = MainViewModel(fakeRepository)
        val expected =
            MainUiState(
                FAKE_COURSES.map { course: Course ->
                    CourseItem(course, selected = course == COURSE_20)
                },
            )

        // when
        mainViewModel.select(CourseItem(COURSE_20, selected = false))

        // then
        assertThat(mainViewModel.state.getOrAwaitValue()).isEqualTo(expected)
    }

    @Test
    fun `이미 선택된 코스가 선택되면 해당 코스가 유지된다`() {
        // given
        mainViewModel = MainViewModel(fakeRepository)
        mainViewModel.select(CourseItem(COURSE_20, selected = false))

        val expected =
            MainUiState(
                FAKE_COURSES.map { course: Course ->
                    CourseItem(course, selected = course == COURSE_20)
                },
            )

        // when
        mainViewModel.select(CourseItem(COURSE_20, selected = true))

        // then
        assertThat(mainViewModel.state.getOrAwaitValue()).isEqualTo(expected)
    }
}
