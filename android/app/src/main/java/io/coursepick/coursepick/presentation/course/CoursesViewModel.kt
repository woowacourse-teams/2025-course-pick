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
                    query = "",
                    originalCourses = emptyList(),
                    status = UiStatus.Loading,
                ),
            )
        val state: LiveData<CoursesUiState> get() = _state

        private val _event: MutableSingleLiveData<CoursesUiEvent> = MutableSingleLiveData()
        val event: SingleLiveData<CoursesUiEvent> get() = _event

        private var writeFavoriteJob: Job? = null
        private val pendingFavoriteWrites: MutableMap<String, Boolean> = mutableMapOf()

        init {
            checkNetwork()
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

            val oldCourses: List<CourseItem> = state.value?.courses ?: return

            val selectedIndex = oldCourses.indexOf(course)
            if (selectedIndex == -1) return

            val newCourses: List<CourseItem> = newCourses(oldCourses, course)
            _state.value = state.value?.copy(originalCourses = newCourses)
            _event.value = CoursesUiEvent.SelectCourseManually(course)
        }

        fun toggleFavorite(toggledCourse: CourseItem) {
            pendingFavoriteWrites[toggledCourse.id] = !toggledCourse.favorite

            state.value?.courses?.let { courses: List<CourseItem> ->
                val newCourses =
                    courses.map { course: CourseItem ->
                        if (course.id == toggledCourse.id) course.copy(favorite = !course.favorite) else course
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
            _state.value = state.value?.copy(status = UiStatus.Loading)

            val favoritedCourseIds: Set<String> = favoritesRepository.favoriteCourseIds()

            viewModelScope.launch {
                runCatching {
                    val courses =
                        courseRepository.courses(
                            scope = scope,
                            mapCoordinate = mapCoordinate,
                            userCoordinate = userCoordinate,
                        )
                    courses
                        .sortedBy(Course::distance)
                        .mapIndexed { index: Int, course: Course ->
                            CourseItem(
                                course = course,
                                selected = index == 0,
                                favorite = favoritedCourseIds.contains(course.id),
                            )
                        }
                }.onSuccess { courses: List<CourseItem> ->
                    Logger.log(Logger.Event.Success("fetch_courses"))
                    _state.value =
                        state.value?.copy(
                            originalCourses = courses,
                            status = UiStatus.Success,
                        )
                }.onFailure { exception: Throwable ->
                    Logger.log(
                        Logger.Event.Failure("fetch_courses"),
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
                        state.value
                            ?.copy(
                                originalCourses = emptyList(),
                                status = UiStatus.Failure,
                            )
                    _event.value = CoursesUiEvent.FetchCourseFailure
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
                                originalCourses = courseItems,
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
            val oldCourses: List<CourseItem> = state.value?.courses ?: return
            val newCourses: List<CourseItem> = newCourses(oldCourses, course)
            _state.value =
                state.value?.copy(
                    originalCourses = newCourses,
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
                    _event.value = CoursesUiEvent.ShowNotice(notice)
                }
            }
        }

        private fun newCourses(
            oldCourses: List<CourseItem>,
            selectedCourse: CourseItem,
        ): List<CourseItem> =
            oldCourses.map { course: CourseItem ->
                if (course == selectedCourse) {
                    course.copy(selected = true)
                } else {
                    course.copy(selected = false)
                }
            }

        companion object {
            private const val DEBOUNCE_LIMIT_TIME = 500L
        }
    }
