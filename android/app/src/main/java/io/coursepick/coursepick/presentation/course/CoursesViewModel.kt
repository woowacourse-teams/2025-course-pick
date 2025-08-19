package io.coursepick.coursepick.presentation.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.presentation.CoursePickApplication
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.ui.MutableSingleLiveData
import io.coursepick.coursepick.presentation.ui.SingleLiveData
import kotlinx.coroutines.launch
import okio.IOException

class CoursesViewModel(
    private val courseRepository: CourseRepository,
) : ViewModel() {
    private val _state: MutableLiveData<CoursesUiState> =
        MutableLiveData(
            CoursesUiState(
                query = "",
                courses = emptyList(),
                isLoading = true,
            ),
        )
    val state: LiveData<CoursesUiState> get() = _state

    private val _event: MutableSingleLiveData<CoursesUiEvent> = MutableSingleLiveData()
    val event: SingleLiveData<CoursesUiEvent> get() = _event

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

    fun fetchCourses(
        mapCoordinate: Coordinate,
        userCoordinate: Coordinate?,
        scope: Scope = Scope.default(),
    ) {
        _state.value =
            state.value?.copy(
                isLoading = true,
                isFailure = false,
            )
        viewModelScope.launch {
            try {
                val courses = courseRepository.courses(mapCoordinate, userCoordinate, scope)
                Logger.log(Logger.Event.Success("fetch_courses"))
                val courseItems: List<CourseItem> =
                    courses
                        .sortedBy { course: Course -> course.distance }
                        .mapIndexed { index: Int, course: Course ->
                            CourseItem(
                                course,
                                index == 0,
                            )
                        }
                _state.value =
                    state.value?.copy(courses = courseItems, isLoading = false, isFailure = false)
                _event.value = CoursesUiEvent.FetchCourseSuccess(courseItems.firstOrNull())
            } catch (exception: IOException) {
                _state.value =
                    state.value?.copy(courses = emptyList(), isLoading = false, isFailure = true)
                _event.value = CoursesUiEvent.NoInternet
            } catch (exception: Exception) {
                Logger.log(
                    Logger.Event.Failure("fetch_courses"),
                    "message" to exception.message.toString(),
                )
                _state.value =
                    state.value?.copy(courses = emptyList(), isLoading = false, isFailure = true)
                _event.value = CoursesUiEvent.FetchCourseFailure
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
                    return CoursesViewModel(application.courseRepository) as T
                }
            }
    }
}
