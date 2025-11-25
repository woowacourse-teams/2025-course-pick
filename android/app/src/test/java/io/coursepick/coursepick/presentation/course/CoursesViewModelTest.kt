package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.fixture.COORDINATE_FIXTURE
import io.coursepick.coursepick.domain.fixture.COURSE_FIXTURE_20
import io.coursepick.coursepick.domain.fixture.FAKE_COURSES
import io.coursepick.coursepick.presentation.extension.CoroutinesTestExtension
import io.coursepick.coursepick.presentation.extension.InstantTaskExecutorExtension
import io.coursepick.coursepick.presentation.fixtures.FakeCourseRepository
import io.coursepick.coursepick.presentation.fixtures.FakeFavoritesRepository
import io.coursepick.coursepick.presentation.fixtures.FakeNetworkMonitor
import io.coursepick.coursepick.presentation.fixtures.FakeNoticeRepository
import io.coursepick.coursepick.presentation.fixtures.NOTICE_FIXTURE
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
    private val fakeCourseRepository = FakeCourseRepository()
    private val fakeFavoritesRepository = FakeFavoritesRepository()
    private val fakeNoticeRepository = FakeNoticeRepository()
    private val fakeNetworkMonitor = FakeNetworkMonitor()
    private lateinit var mainViewModel: CoursesViewModel

    @BeforeEach
    fun setUp() {
        mainViewModel =
            CoursesViewModel(
                fakeCourseRepository,
                fakeFavoritesRepository,
                fakeNoticeRepository,
                fakeNetworkMonitor,
            )
        mainViewModel.fetchCourses(COORDINATE_FIXTURE, null, Scope.default())
    }

    @Test
    fun `초기 상태에서는 가장 가까운 코스가 선택된다`() {
        // given
        val expected =
            CoursesUiState(
                originalCourses =
                    FAKE_COURSES.mapIndexed { index: Int, course: Course ->
                        CourseItem(course, selected = index == 0, favorite = false)
                    },
                status = UiStatus.Success,
                verifiedLocations = NOTICE_FIXTURE,
            )
        val actual: CoursesUiState = mainViewModel.state.getOrAwaitValue()

        // then
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `하나의 코스를 선택하면 나머지는 해제되고 해당 코스만 선택된다`() {
        // given
        val expected =
            CoursesUiState(
                FAKE_COURSES.map { course: Course ->
                    CourseItem(course, selected = course == COURSE_FIXTURE_20, favorite = false)
                },
                status = UiStatus.Success,
                verifiedLocations = NOTICE_FIXTURE,
            )

        // when
        mainViewModel.select(CourseItem(COURSE_FIXTURE_20, selected = false, favorite = false))

        // then
        Assertions.assertThat(mainViewModel.state.getOrAwaitValue()).isEqualTo(expected)
    }

    @Test
    fun `이미 선택된 코스가 선택되면 해당 코스가 유지된다`() {
        // given
        mainViewModel.select(CourseItem(COURSE_FIXTURE_20, selected = false, favorite = false))

        val expected =
            CoursesUiState(
                FAKE_COURSES.map { course: Course ->
                    CourseItem(course, selected = course == COURSE_FIXTURE_20, favorite = false)
                },
                status = UiStatus.Success,
                verifiedLocations = NOTICE_FIXTURE,
            )

        // when
        mainViewModel.select(CourseItem(COURSE_FIXTURE_20, selected = true, favorite = false))

        // then
        Assertions.assertThat(mainViewModel.state.getOrAwaitValue()).isEqualTo(expected)
    }
}
