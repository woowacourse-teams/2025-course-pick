package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.fixture.COORDINATE_FIXTURE
import io.coursepick.coursepick.domain.fixture.COURSE_FIXTURE_20
import io.coursepick.coursepick.domain.fixture.FAKE_COURSES
import io.coursepick.coursepick.presentation.extension.CoroutinesTestExtension
import io.coursepick.coursepick.presentation.extension.InstantTaskExecutorExtension
import io.coursepick.coursepick.presentation.fixtures.FakeRepository
import io.coursepick.coursepick.presentation.ui.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CoursesViewModelTest {
    private val fakeRepository = FakeRepository()
    private lateinit var mainViewModel: CoursesViewModel

    @BeforeEach
    fun setUp() {
        mainViewModel = CoursesViewModel(fakeRepository)
        mainViewModel.fetchCourses(COORDINATE_FIXTURE)
    }

    @Test
    fun `초기 상태에서는 가장 가까운 코스가 선택된다`() {
        // given
        val expected =
            CoursesUiState(
                FAKE_COURSES.mapIndexed { index: Int, course: Course ->
                    CourseItem(course, selected = index == 0)
                },
            )
        val actual = mainViewModel.state.getOrAwaitValue()

        // then
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `하나의 코스를 선택하면 나머지는 해제되고 해당 코스만 선택된다`() {
        // given
        val expected =
            CoursesUiState(
                FAKE_COURSES.map { course: Course ->
                    CourseItem(course, selected = course == COURSE_FIXTURE_20)
                },
            )

        // when
        mainViewModel.select(CourseItem(COURSE_FIXTURE_20, selected = false))

        // then
        Assertions.assertThat(mainViewModel.state.getOrAwaitValue()).isEqualTo(expected)
    }

    @Test
    fun `이미 선택된 코스가 선택되면 해당 코스가 유지된다`() {
        // given
        mainViewModel.select(CourseItem(COURSE_FIXTURE_20, selected = false))

        val expected =
            CoursesUiState(
                FAKE_COURSES.map { course: Course ->
                    CourseItem(course, selected = course == COURSE_FIXTURE_20)
                },
            )

        // when
        mainViewModel.select(CourseItem(COURSE_FIXTURE_20, selected = true))

        // then
        Assertions.assertThat(mainViewModel.state.getOrAwaitValue()).isEqualTo(expected)
    }
}
