package io.coursepick.coursepick.presentation.createcustomcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.Result
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.customcourse.CustomCourseFailure
import io.coursepick.coursepick.domain.customcourse.CustomCourseRepository
import io.coursepick.coursepick.domain.customcourse.DraftCourse
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCustomCourseViewModel
    @Inject
    constructor(
        private val repository: CustomCourseRepository,
    ) : ViewModel() {
        private val _event = MutableSharedFlow<CreateCustomCourseUiEvent>()
        val event: SharedFlow<CreateCustomCourseUiEvent> get() = _event.asSharedFlow()

        private val _showSubmitDialog = MutableStateFlow(false)
        val showSubmitDialog: StateFlow<Boolean> get() = _showSubmitDialog.asStateFlow()

        private val _showDiscardDialog = MutableStateFlow(false)
        val showDiscardDialog: StateFlow<Boolean> get() = _showDiscardDialog.asStateFlow()

        private val _courseName = MutableStateFlow("")
        val courseName: StateFlow<String> get() = _courseName.asStateFlow()

        private val _segments = MutableStateFlow<List<DraftSegment>>(emptyList())
        val segments: StateFlow<List<DraftSegment>> get() = _segments.asStateFlow()

        val length: StateFlow<Length> =
            segments
                .map { segments: List<DraftSegment> ->
                    Length(segments.sumOf { segment: DraftSegment -> segment.length.meter.value })
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = Length(0),
                )

        val waypoints: List<Coordinate> get() = segments.value.mapNotNull { segment: DraftSegment -> segment.coordinates.lastOrNull() }

        fun addWaypoint(waypoint: Coordinate) {
            viewModelScope.launch {
                val origin: Coordinate = waypoints.lastOrNull() ?: waypoint
                val rawSegment: DraftSegment = repository.draftSegment(origin, waypoint)
                val adjustedSegment: DraftSegment =
                    rawSegment
                        .copy(coordinates = rawSegment.coordinates.dropLast(1), length = rawSegment.length)
                        .let { segment: DraftSegment ->
                            if (waypoints.isEmpty()) {
                                val firstWaypoint = segment.coordinates.lastOrNull() ?: return@launch
                                DraftSegment(listOf(firstWaypoint), segment.length)
                            } else {
                                segment
                            }
                        }

                _segments.value += adjustedSegment
                _event.emit(CreateCustomCourseUiEvent.NewSegment(adjustedSegment))
            }
        }

        fun removeLastWaypoint() {
            viewModelScope.launch {
                _segments.value = segments.value.dropLast(1)
                _event.emit(CreateCustomCourseUiEvent.RemoveLastWaypoint)
            }
        }

        fun handleSubmitAction() {
            if (segments.value.isEmpty() || length.value < MINIMUM_COURSE_LENGTH) {
                viewModelScope.launch {
                    _event.emit(CreateCustomCourseUiEvent.CourseLengthTooShort)
                }
            } else {
                _showSubmitDialog.value = true
            }
        }

        fun dismissSubmitDialog() {
            _showSubmitDialog.value = false
            _courseName.value = ""
        }

        fun handleExitAction() {
            if (segments.value.isEmpty()) {
                viewModelScope.launch {
                    _event.emit(CreateCustomCourseUiEvent.Exit)
                }
            } else {
                _showDiscardDialog.value = true
            }
        }

        fun dismissExitDialog() {
            _showDiscardDialog.value = false
        }

        fun updateCourseName(courseName: String) {
            _courseName.value = courseName.lines().joinToString("")
        }

        fun submitCourse() {
            viewModelScope.launch {
                val courseName =
                    runCatching { CourseName(courseName.value) }.getOrElse { exception: Throwable ->
                        if (exception is IllegalArgumentException) {
                            _event.emit(CreateCustomCourseUiEvent.InvalidCourseName)
                        } else {
                            _event.emit(CreateCustomCourseUiEvent.UnknownError)
                        }
                        return@launch
                    }

                val result: Result<Unit, CustomCourseFailure> =
                    repository.submitCourse(DraftCourse(CourseName(courseName.value), waypoints))

                _event.emit(
                    when (result) {
                        is Result.Success<Unit> -> {
                            CreateCustomCourseUiEvent.CreateCustomCourseSuccess
                        }

                        is Result.Failure<CustomCourseFailure> -> {
                            when (result.type) {
                                CustomCourseFailure.InvalidCourseName -> CreateCustomCourseUiEvent.InvalidCourseName
                                CustomCourseFailure.UnauthorizedUser -> CreateCustomCourseUiEvent.UnauthorizedUser
                                CustomCourseFailure.Unknown -> CreateCustomCourseUiEvent.UnknownError
                            }
                        }
                    },
                )
            }
        }

        companion object {
            private val MINIMUM_COURSE_LENGTH = Length(1)
        }
    }
