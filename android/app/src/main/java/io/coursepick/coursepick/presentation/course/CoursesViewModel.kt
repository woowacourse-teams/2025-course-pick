package io.coursepick.coursepick.presentation.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import io.coursepick.coursepick.data.NetworkMonitor
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import io.coursepick.coursepick.presentation.CoursePickApplication
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.ui.MutableSingleLiveData
import io.coursepick.coursepick.presentation.ui.SingleLiveData
import kotlinx.coroutines.launch
import java.io.IOException

class CoursesViewModel(
    private val courseRepository: CourseRepository,
    private val favoritesRepository: FavoritesRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val _state: MutableLiveData<CoursesUiState> =
        MutableLiveData(
            CoursesUiState(
                query = "",
                courses = emptyList(),
                isLoading = true,
                isNoInternet = false,
            ),
        )
    val state: LiveData<CoursesUiState> get() = _state

    private val _event: MutableSingleLiveData<CoursesUiEvent> = MutableSingleLiveData()
    val event: SingleLiveData<CoursesUiEvent> get() = _event

    init {
        checkNetwork()
    }

    private fun checkNetwork() {
        if (!networkMonitor.isConnected()) {
            _state
                .value =
                state.value?.copy(
                    isLoading = false,
                    isNoInternet = true,
                )
        }
    }

    fun select(selectedCourse: CourseItem) {
        if (selectedCourse.selected) {
            _event.value = CoursesUiEvent.SelectNewCourse(selectedCourse)
            return
        }

        val oldCourses: List<CourseItem> = state.value?.courses ?: return

        val selectedIndex = oldCourses.indexOf(selectedCourse)
        if (selectedIndex == -1) return

        val newCourses: List<CourseItem> = newCourses(oldCourses, selectedCourse)
        _state.value = state.value?.copy(courses = newCourses)
        _event.value = CoursesUiEvent.SelectNewCourse(selectedCourse)
    }

    fun toggleFavorite(toggledCourse: CourseItem) {
        state.value?.courses?.let { courses: List<CourseItem> ->
            val newCourses =
                courses.map { course: CourseItem ->
                    if (course.id == toggledCourse.id) course.copy(favorite = !course.favorite) else course
                }
            _state.value = state.value?.copy(courses = newCourses)
        }
        if (toggledCourse.favorite) {
            favoritesRepository.removeFavorite(toggledCourse.id)
        } else {
            favoritesRepository.addFavorite(toggledCourse.id)
        }
    }

    fun fetchCourses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
        scope: Scope = Scope.default(),
    ) {
        _state.value =
            state.value?.copy(
                isLoading = true,
                isFailure = false,
                isNoInternet = false,
            )

        val favoritedCourseIds: Set<String> = favoritesRepository.favoritedCourseIds()

        viewModelScope.launch {
            runCatching {
                val courses = courseRepository.courses(mapCoordinate, userCoordinate, scope)
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
                _state
                    .value =
                    state.value?.copy(
                        courses = courses,
                        isLoading = false,
                        isFailure = false,
                    )
            }.onFailure { exception: Throwable ->
                Logger.log(
                    Logger.Event.Failure("fetch_courses"),
                    "message" to exception.message.toString(),
                )
                if (exception is IOException) {
                    _state
                        .value =
                        state.value?.copy(
                            courses = emptyList(),
                            isLoading = false,
                            isFailure = false,
                            isNoInternet = true,
                        )
                    return@onFailure
                }
                _state.value =
                    state.value
                        ?.copy(
                            courses = emptyList(),
                            isLoading = false,
                            isFailure = true,
                        )
                _event.value = CoursesUiEvent.FetchCourseFailure
            }
        }
    }

    fun fetchFavorites() {
        _state.value =
            state.value?.copy(
                isLoading = true,
                isFailure = false,
                isNoInternet = false,
            )

        val favoritedCourseIds: Set<String> = favoritesRepository.favoritedCourseIds()

        viewModelScope.launch {
            runCatching {
                courseRepository.coursesById(favoritedCourseIds.toList())
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
                            courses = courseItems,
                            isLoading = false,
                            isNoInternet = false,
                        )
            }.onFailure { throwable: Throwable ->
                Logger.log(
                    Logger.Event.Failure("fetch_courses"),
                    "message" to throwable.message.toString(),
                )
                _state.value =
                    state.value
                        ?.copy(
                            courses = emptyList(),
                            isLoading = false,
                            isNoInternet = true,
                        )
            }
        }
    }

    fun fetchNearestCoordinate(
        selectedCourse: CourseItem,
        location: Coordinate,
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
        val Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY]) as CoursePickApplication
                    return CoursesViewModel(
                        application.courseRepository,
                        application.favoritesRepository,
                        application.networkMonitor,
                    ) as T
                }
            }
    }
}
