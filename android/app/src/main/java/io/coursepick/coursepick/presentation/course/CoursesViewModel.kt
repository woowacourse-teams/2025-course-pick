package io.coursepick.coursepick.presentation.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.NetworkMonitor
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.CoursesPage
import io.coursepick.coursepick.domain.course.Kilometer
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.domain.notice.NoticeRepository
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.filter.CourseFilter
import io.coursepick.coursepick.presentation.model.Difficulty
import io.coursepick.coursepick.presentation.routefinder.RouteFinderApplication
import io.coursepick.coursepick.presentation.ui.MutableSingleLiveData
import io.coursepick.coursepick.presentation.ui.SingleLiveData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel
@Inject
constructor(
    private val courseRepository: CourseRepository,
    private val favoritesRepository: FavoritesRepository,
    private val noticeRepository: NoticeRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val _state: MutableLiveData<CoursesUiState> =
        MutableLiveData(
            CoursesUiState(
                originalCourses = emptyList(),
                query = "",
                status = UiStatus.Loading,
            ),
        )
    val state: LiveData<CoursesUiState> get() = _state

    private val _event: MutableSingleLiveData<CoursesUiEvent> = MutableSingleLiveData()
    val event: SingleLiveData<CoursesUiEvent> get() = _event

    private var writeFavoriteJob: Job? = null
    private val pendingFavoriteWrites: MutableMap<String, Boolean> = mutableMapOf()

    private var page: Int = 0
    private var hasNext: Boolean = false

    private var lastMapCoordinate: Coordinate? = null
    private var lastUserCoordinate: Coordinate? = null
    private var lastScope: Scope? = null

    init {
        checkNetwork()
        fetchVerifiedLocations()
    }

    private fun checkNetwork() {
        if (!networkMonitor.isConnected()) {
            _state.value = state.value?.copy(status = UiStatus.NoInternet)
        }
    }

    fun select(course: CourseItem) {
        if (course.selected) {
            _event.value = CoursesUiEvent.SelectCourseManually(course)
            return
        }

        val oldCourses: List<CourseListItem> = state.value?.courses ?: return

        val selectedIndex = oldCourses.indexOf(CourseListItem.Course(course))
        if (selectedIndex == -1) return

        val newCourseItems: List<CourseListItem> = newCoursesListItem(oldCourses, course)
        _state.value = state.value?.copy(originalCourses = newCourseItems)
        _event.value = CoursesUiEvent.SelectCourseManually(course)
    }

    fun toggleFavorite(toggledCourse: CourseItem) {
        pendingFavoriteWrites[toggledCourse.id] = !toggledCourse.favorite

        state.value?.originalCourses?.let { courses: List<CourseListItem> ->
            val newCourses =
                courses.map { item: CourseListItem ->
                    when (item) {
                        is CourseListItem.Course ->
                            if (item.item.id == toggledCourse.id) {
                                CourseListItem.Course(item.item.copy(favorite = !item.item.favorite))
                            } else {
                                item
                            }

                        is CourseListItem.Loading -> item
                    }
                }
            _state.value = state.value?.copy(originalCourses = newCourses)
        }

        updateFavorites()
    }

    private fun updateFavorites() {
        writeFavoriteJob?.cancel()

        writeFavoriteJob =
            viewModelScope.launch {
                delay(DEBOUNCE_LIMIT_TIME)

                pendingFavoriteWrites.toMap().forEach { courseId: String, favorite: Boolean ->
                    if (favorite) {
                        favoritesRepository.addFavoriteCourse(courseId)
                    } else {
                        favoritesRepository.removeFavoriteCourse(courseId)
                    }
                }
                pendingFavoriteWrites.clear()
            }
    }

    fun fetchCourses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
        scope: Scope = Scope.default(),
    ) {
        _state.value =
            state.value?.copy(
                originalCourses = listOf(CourseListItem.Loading),
                status = UiStatus.Loading,
            )

        viewModelScope.launch {
            runCatching {
                courseRepository.courses(
                    scope = scope,
                    page = 0,
                    mapCoordinate = mapCoordinate,
                    userCoordinate = userCoordinate,
                )
            }.onSuccess { coursesPage: CoursesPage ->
                Logger.log(Logger.Event.Success("fetch_courses_new"))

                val favoritedCourseIds: Set<String> = favoritesRepository.favoriteCourseIds()

                val courseItems: List<CourseItem> =
                    coursesPage.courses
                        .sortedBy(Course::distance)
                        .mapIndexed { index, course ->
                            CourseItem(
                                course = course,
                                selected = index == 0,
                                favorite = favoritedCourseIds.contains(course.id),
                            )
                        }

                lastMapCoordinate = mapCoordinate
                lastUserCoordinate = userCoordinate
                lastScope = scope

                page = 0
                hasNext = coursesPage.hasNext

                _state.value =
                    state.value?.copy(
                        originalCourses = courseItems.map(CourseListItem::Course),
                        status = UiStatus.Success,
                    )
            }.onFailure { exception: Throwable ->
                Logger.log(
                    Logger.Event.Failure("fetch_courses_new"),
                    "message" to exception.message.toString(),
                )
                if (exception is NoNetworkException) {
                    _state.value =
                        state.value?.copy(
                            originalCourses = emptyList(),
                            status = UiStatus.NoInternet,
                        )
                    return@onFailure
                }
                _state.value =
                    state.value?.copy(
                        originalCourses = emptyList(),
                        status = UiStatus.Failure,
                    )
                _event.value = CoursesUiEvent.FetchCourseFailure
            }
        }
    }

    fun fetchNextCourses() {
        val currentStatus: UiStatus? = state.value?.status
        if (currentStatus == UiStatus.Loading || !hasNext) return

        val existingCourses: List<CourseListItem> = state.value?.originalCourses ?: emptyList()
        _state.value =
            state.value?.copy(
                originalCourses = existingCourses + CourseListItem.Loading,
                status = UiStatus.Loading,
            )

        val mapCoordinate: Coordinate = lastMapCoordinate ?: return
        val userCoordinate: Coordinate? = lastUserCoordinate
        val scope: Scope = lastScope ?: return

        viewModelScope.launch {
            runCatching {
                val nextPage: Int = page + 1
                courseRepository.courses(
                    scope = scope,
                    page = nextPage,
                    mapCoordinate = mapCoordinate,
                    userCoordinate = userCoordinate,
                )
            }.onSuccess { coursesPage: CoursesPage ->
                Logger.log(Logger.Event.Success("fetch_next_courses"))

                val favoritedCourseIds: Set<String> = favoritesRepository.favoriteCourseIds()

                val existingCoursesWithoutLoading =
                    (state.value?.originalCourses ?: emptyList())
                        .filterNot { courseListItem: CourseListItem -> courseListItem is CourseListItem.Loading }

                val newCourses =
                    coursesPage.courses.map { course: Course ->
                        CourseListItem.Course(
                            CourseItem(
                                course = course,
                                selected = false,
                                favorite = favoritedCourseIds.contains(course.id),
                            ),
                        )
                    }

                page += 1
                hasNext = coursesPage.hasNext

                _state.value =
                    state.value?.copy(
                        originalCourses = existingCoursesWithoutLoading + newCourses,
                        status = UiStatus.Success,
                    )
            }.onFailure { exception: Throwable ->
                Logger.log(
                    Logger.Event.Failure("fetch_next_courses"),
                    "message" to exception.message.toString(),
                )

                val existingCoursesWithoutLoading =
                    (state.value?.originalCourses ?: emptyList())
                        .filterNot { courseListItem: CourseListItem -> courseListItem is CourseListItem.Loading }

                _state.value =
                    state.value?.copy(
                        originalCourses = existingCoursesWithoutLoading,
                        status = UiStatus.Failure,
                    )
                _event.value = CoursesUiEvent.FetchNextCoursesFailure
            }
        }
    }

    fun fetchFavorites() {
        _state.value = state.value?.copy(status = UiStatus.Loading)

        val favoritedCourseIds: Set<String> = favoritesRepository.favoriteCourseIds()
        if (favoritedCourseIds.isEmpty()) {
            _state.value =
                state.value?.copy(
                    originalCourses = emptyList(),
                    status = UiStatus.Success,
                )
            return
        }

        viewModelScope.launch {
            runCatching {
                courseRepository.courses(favoritedCourseIds.toList())
            }.onSuccess { courses: List<Course> ->
                val courseItems: List<CourseItem> =
                    courses.map { course: Course ->
                        CourseItem(
                            course = course,
                            selected = false,
                            favorite = true,
                        )
                    }
                _state.value =
                    state.value
                        ?.copy(
                            originalCourses = courseItems.map(CourseListItem::Course),
                            status = UiStatus.Success,
                        )
            }.onFailure { exception: Throwable ->
                Logger.log(
                    Logger.Event.Failure("fetch_courses"),
                    "message" to exception.message.toString(),
                )
                if (exception is NoNetworkException) {
                    _state.value =
                        state.value
                            ?.copy(
                                originalCourses = emptyList(),
                                status = UiStatus.NoInternet,
                            )
                    return@onFailure
                }
                _state.value =
                    state.value
                        ?.copy(
                            originalCourses = emptyList(),
                            status = UiStatus.Failure,
                        )
                _event.value = CoursesUiEvent.FetchCourseFailure
            }
        }
    }

    fun fetchRouteToCourse(
        course: CourseItem,
        origin: Coordinate,
    ) {
        val oldCourses: List<CourseListItem> = state.value?.courses ?: return
        val newCourseItems: List<CourseListItem> = newCoursesListItem(oldCourses, course)
        _state.value =
            state.value?.copy(
                originalCourses = newCourseItems,
                status = UiStatus.Loading,
            )

        val selectedCourse: CourseItem = course.copy(selected = true)
        viewModelScope.launch {
            runCatching {
                courseRepository.routeToCourse(selectedCourse.course, origin)
            }.onSuccess { route: List<Coordinate> ->
                Logger.log(Logger.Event.Success("fetch_route_to_course"))
                _state.value =
                    state.value?.copy(
                        status = UiStatus.Success,
                    )
                _event.value = CoursesUiEvent.FetchRouteToCourseSuccess(route, selectedCourse)
            }.onFailure { error: Throwable ->
                when (error) {
                    is NoNetworkException -> {
                        _state.value =
                            state.value?.copy(
                                status = UiStatus.NoInternet,
                            )
                    }

                    else -> {
                        _state.value =
                            state.value?.copy(
                                status = UiStatus.Failure,
                            )
                    }
                }
                Logger.log(
                    Logger.Event.Failure("fetch_route_to_course"),
                    "message" to error.message.toString(),
                )
                _event.value = CoursesUiEvent.FetchRouteToCourseFailure
            }
        }
    }

    fun fetchNearestCoordinate(
        selectedCourse: CourseItem,
        location: Coordinate,
        routeFinder: RouteFinderApplication.ThirdParty,
    ) {
        viewModelScope.launch {
            runCatching {
                courseRepository.nearestCoordinate(selectedCourse.course, location)
            }.onSuccess { nearest: Coordinate ->
                Logger.log(Logger.Event.Success("fetch_nearest_coordinate"))
                _event.value =
                    CoursesUiEvent.FetchNearestCoordinateSuccess(
                        origin = location,
                        destination = nearest,
                        destinationName = selectedCourse.name,
                        routeFinder,
                    )
            }.onFailure { error: Throwable ->
                Logger.log(
                    Logger.Event.Failure("fetch_nearest_coordinate"),
                    "message" to error.message.toString(),
                )
                _event.value = CoursesUiEvent.FetchNearestCoordinateFailure
            }
        }
    }

    fun setQuery(query: String) {
        _state.value = state.value?.copy(query = query)
    }

    fun resetFilterToDefault() {
        _state.value = state.value?.copy(courseFilter = CourseFilter.None)
    }

    fun toggleDifficulty(difficulty: Difficulty) {
        val updatedDifficulties =
            state.value
                ?.courseFilter
                ?.difficulties
                ?.toMutableSet()
                ?.apply {
                    if (contains(difficulty)) remove(difficulty) else add(difficulty)
                }
                ?: mutableSetOf(difficulty)

        val courseFilter =
            state.value?.courseFilter?.copy(_difficulties = updatedDifficulties)
                ?: CourseFilter.None.copy(_difficulties = updatedDifficulties)

        _state.value = state.value?.copy(courseFilter = courseFilter)
    }

    fun updateLengthRange(
        min: Double,
        max: Double,
    ) {
        val minKm = Kilometer(min)
        val maxKm = Kilometer(max)

        val currentRange = state.value?.courseFilter?.lengthRange
        if (currentRange?.start == minKm && currentRange.endInclusive == maxKm) return

        val updatedLengthRange = minKm..maxKm

        val updatedCourseFilter =
            state.value?.courseFilter?.copy(lengthRange = updatedLengthRange)
                ?: CourseFilter.None.copy(lengthRange = updatedLengthRange)
        _state.value = state.value?.copy(courseFilter = updatedCourseFilter)
    }

    fun restore(coursesUiState: CoursesUiState) {
        _state.value = coursesUiState
    }

    fun fetchNotice(id: String) {
        viewModelScope.launch {
            runCatching {
                noticeRepository.notice(id)
            }.onSuccess { notice: Notice ->
                _state.value = state.value?.copy(notice = notice)
            }
        }
    }

    fun showVerifiedLocations() {
        _state.value = state.value?.copy(showVerifiedLocations = true)
    }

    fun dismissVerifiedLocations() {
        _state.value = state.value?.copy(showVerifiedLocations = false)
    }

    fun dismissNotice() {
        _state.value = state.value?.copy(notice = null)
    }

    private fun fetchVerifiedLocations() {
        viewModelScope.launch {
            runCatching {
                noticeRepository.verifiedLocations()
            }.onSuccess { verifiedLocations: Notice ->
                _state.value = state.value?.copy(verifiedLocations = verifiedLocations)
            }
        }
    }

    private fun newCoursesListItem(
        oldCourses: List<CourseListItem>,
        selectedCourse: CourseItem,
    ): List<CourseListItem> =
        oldCourses.map { courseListItem: CourseListItem ->
            if (courseListItem is CourseListItem.Course) {
                if (courseListItem.item == selectedCourse) {
                    CourseListItem.Course(courseListItem.item.copy(selected = true))
                } else {
                    CourseListItem.Course(courseListItem.item.copy(selected = false))
                }
            } else {
                courseListItem
            }
        }

    companion object {
        private const val DEBOUNCE_LIMIT_TIME = 500L
    }
}
