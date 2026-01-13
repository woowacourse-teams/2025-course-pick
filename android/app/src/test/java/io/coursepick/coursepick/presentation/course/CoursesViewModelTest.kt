package io.coursepick.coursepick.presentation.course

import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CoursesPage
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
    private lateinit var fakeCourseRepository: FakeCourseRepository
    private val fakeFavoritesRepository = FakeFavoritesRepository()
    private val fakeNoticeRepository = FakeNoticeRepository()
    private val fakeNetworkMonitor = FakeNetworkMonitor()
    private lateinit var mainViewModel: CoursesViewModel

    @BeforeEach
    fun setUp() {
        fakeCourseRepository = FakeCourseRepository()
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
                        CourseListItem.Course(
                            CourseItem(course, selected = index == 0, favorite = false),
                        )
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
                    CourseListItem.Course(
                        CourseItem(
                            course,
                            selected = course == COURSE_FIXTURE_20,
                            favorite = false,
                        ),
                    )
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
                    CourseListItem.Course(
                        CourseItem(
                            course,
                            selected = course == COURSE_FIXTURE_20,
                            favorite = false,
                        ),
                    )
                },
                status = UiStatus.Success,
                verifiedLocations = NOTICE_FIXTURE,
            )

        // when
        mainViewModel.select(CourseItem(COURSE_FIXTURE_20, selected = true, favorite = false))

        // then
        Assertions.assertThat(mainViewModel.state.getOrAwaitValue()).isEqualTo(expected)
    }

    @Test
    fun `첫 페이지 로드 실패 시 에러 상태로 업데이트된다`() {
        // given
        fakeCourseRepository.shouldThrowError = true
        val viewModel =
            CoursesViewModel(
                fakeCourseRepository,
                fakeFavoritesRepository,
                fakeNoticeRepository,
                fakeNetworkMonitor,
            )

        // when
        viewModel.fetchCourses(COORDINATE_FIXTURE, null, Scope.default())

        println(">>> [1] ${viewModel.state.value.status}")
        // then
        val state: CoursesUiState = viewModel.state.getOrAwaitValue()
        println(">>> [2] ${viewModel.state.value.status}")
        Assertions.assertThat(state.status).isEqualTo(UiStatus.Failure)
        Assertions.assertThat(state.originalCourses).isEmpty()
    }

    @Test
    fun `다음 페이지 로드 성공 시 기존 코스와 새 코스가 병합된다`() {
        // given
        val secondPageCourses = listOf(COURSE_FIXTURE_20)
        fakeCourseRepository.customCoursesPage =
            CoursesPage(courses = secondPageCourses, hasNext = false)

        // when
        mainViewModel.fetchNextCourses()

        // then
        val state: CoursesUiState = mainViewModel.state.getOrAwaitValue()
        Assertions.assertThat(state.status).isEqualTo(UiStatus.Success)
        Assertions.assertThat(state.originalCourses.size).isEqualTo(FAKE_COURSES.size + 1)

        val lastCourseListItem = state.originalCourses.last()
        val lastCourse = (lastCourseListItem as CourseListItem.Course).item
        Assertions.assertThat(lastCourse.course).isEqualTo(COURSE_FIXTURE_20)
        Assertions.assertThat(lastCourse.selected).isFalse()
    }

    @Test
    fun `로딩 중일 때 fetchNextCourses 호출 시 중복 요청이 방지된다`() {
        // given
        fakeCourseRepository.customCoursesPage =
            CoursesPage(courses = FAKE_COURSES, hasNext = true)

        val viewModel =
            CoursesViewModel(
                fakeCourseRepository,
                fakeFavoritesRepository,
                fakeNoticeRepository,
                fakeNetworkMonitor,
            )
        viewModel.fetchCourses(COORDINATE_FIXTURE, null, Scope.default())

        val initialState: CoursesUiState = viewModel.state.getOrAwaitValue()
        val initialCourseCount = initialState.originalCourses.size

        // when - fetchNextCourses를 두 번 연속 호출
        fakeCourseRepository.customCoursesPage =
            CoursesPage(courses = listOf(COURSE_FIXTURE_20), hasNext = false)

        viewModel.fetchNextCourses()
        viewModel.fetchNextCourses()

        // then - 한 번만 추가되었는지 확인
        val state: CoursesUiState = viewModel.state.getOrAwaitValue()
        Assertions.assertThat(state.originalCourses.size).isEqualTo(initialCourseCount + 1)
    }

    @Test
    fun `hasNext가 false일 때 fetchNextCourses 호출 시 요청이 방지된다`() {
        // given
        fakeCourseRepository.customCoursesPage =
            CoursesPage(courses = FAKE_COURSES, hasNext = false)

        val viewModel =
            CoursesViewModel(
                fakeCourseRepository,
                fakeFavoritesRepository,
                fakeNoticeRepository,
                fakeNetworkMonitor,
            )
        viewModel.fetchCourses(COORDINATE_FIXTURE, null, Scope.default())

        val initialState: CoursesUiState = viewModel.state.getOrAwaitValue()
        val initialCourseCount = initialState.originalCourses.size

        fakeCourseRepository.customCoursesPage =
            CoursesPage(courses = listOf(COURSE_FIXTURE_20), hasNext = false)

        // when
        viewModel.fetchNextCourses()

        // then
        val state: CoursesUiState = viewModel.state.getOrAwaitValue()
        Assertions.assertThat(state.originalCourses.size).isEqualTo(initialCourseCount)
    }

    @Test
    fun `다음 페이지 로드 실패 시 기존 코스 목록은 유지된다`() {
        // given
        val initialState: CoursesUiState = mainViewModel.state.getOrAwaitValue()
        val initialCourses = initialState.originalCourses

        fakeCourseRepository.shouldThrowError = true

        // when
        mainViewModel.fetchNextCourses()

        // then
        val state: CoursesUiState = mainViewModel.state.getOrAwaitValue()
        Assertions.assertThat(state.status).isEqualTo(UiStatus.Failure)
        Assertions.assertThat(state.originalCourses).isEqualTo(initialCourses)
    }
}
