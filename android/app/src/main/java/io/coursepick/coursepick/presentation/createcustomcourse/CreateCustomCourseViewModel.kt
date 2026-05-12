package io.coursepick.coursepick.presentation.createcustomcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.customcourse.CustomCourseRepository
import io.coursepick.coursepick.domain.customcourse.DraftCourse
import io.coursepick.coursepick.domain.customcourse.DraftSegment
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.auth.AuthFeature
import kotlinx.coroutines.CancellationException
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
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CreateCustomCourseViewModel
    @Inject
    constructor(
        private val customCourseRepository: CustomCourseRepository,
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _event = MutableSharedFlow<CreateCustomCourseUiEvent>()
        val event: SharedFlow<CreateCustomCourseUiEvent> get() = _event.asSharedFlow()

        private val _authDialogState = MutableStateFlow<AuthFeature?>(null)
        val authDialogState: StateFlow<AuthFeature?> get() = _authDialogState.asStateFlow()

        private val _showSubmitDialog = MutableStateFlow(false)
        val showSubmitDialog: StateFlow<Boolean> get() = _showSubmitDialog.asStateFlow()

        private val _showDiscardDialog = MutableStateFlow(false)
        val showDiscardDialog: StateFlow<Boolean> get() = _showDiscardDialog.asStateFlow()

        private val _courseName = MutableStateFlow("")
        val courseName: StateFlow<String> get() = _courseName.asStateFlow()

        private val _isCourseNameOutOfBounds = MutableStateFlow(false)
        val isCourseNameOutOfBounds: StateFlow<Boolean> get() = _isCourseNameOutOfBounds.asStateFlow()

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
                val rawSegment: DraftSegment =
                    runCatching { customCourseRepository.draftSegment(origin, waypoint) }
                        .onSuccess { Logger.log(Logger.Event.Add("create_custom_course_waypoint")) }
                        .getOrElse { exception: Throwable ->
                            Logger.log(Logger.Event.Failure("create_custom_course_waypoint"), "exception" to exception.message.orEmpty())
                            if (exception is NoNetworkException) _event.emit(CreateCustomCourseUiEvent.NoNetwork)
                            return@launch
                        }
                val adjustedSegment: DraftSegment =
                    rawSegment
                        .copy(coordinates = rawSegment.coordinates.dropLast(1))
                        .let { segment: DraftSegment ->
                            if (waypoints.isEmpty()) {
                                val initialWaypoint = segment.coordinates.lastOrNull() ?: return@launch
                                DraftSegment(listOf(initialWaypoint), Length(0))
                            } else {
                                segment
                            }
                        }

                _segments.value += adjustedSegment
                _event.emit(CreateCustomCourseUiEvent.NewSegment(adjustedSegment))
            }
        }

        fun removeLastWaypoint() {
            Logger.log(Logger.Event.Remove("create_custom_course_waypoint"))
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
                Logger.log(Logger.Event.Enter("create_custom_course_submit_dialog"))
                _showSubmitDialog.value = true
            }
        }

        fun dismissSubmitDialog() {
            Logger.log(Logger.Event.Exit("create_custom_course_submit_dialog"))
            _showSubmitDialog.value = false
            _courseName.value = ""
            _isCourseNameOutOfBounds.value = false
        }

        fun handleExitAction() {
            if (segments.value.isEmpty()) {
                viewModelScope.launch {
                    _event.emit(CreateCustomCourseUiEvent.Exit)
                }
            } else {
                Logger.log(Logger.Event.Enter("create_custom_course_discard_dialog"))
                _showDiscardDialog.value = true
            }
        }

        fun dismissExitDialog() {
            Logger.log(Logger.Event.Exit("create_custom_course_discard_dialog"))
            _showDiscardDialog.value = false
        }

        fun updateCourseName(courseName: String) {
            _courseName.value = courseName.lines().joinToString("").take(CourseName.MAX_LENGTH)
            _isCourseNameOutOfBounds.value = courseName.length !in CourseName.MIN_LENGTH..CourseName.MAX_LENGTH
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

                if (authRepository.accessToken() == null) {
                    _authDialogState.value = AuthFeature.CustomCourse
                    return@launch
                }

                runCatching {
                    customCourseRepository.submitCourse(DraftCourse(courseName, waypoints))
                }.onSuccess {
                    Logger.log(
                        Logger.Event.Success("create_custom_course_submit"),
                        "course_name" to courseName,
                        "waypoints_count" to waypoints.size,
                    )
                    _event.emit(CreateCustomCourseUiEvent.CreateCustomCourseSuccess)
                }.onFailure { exception: Throwable ->
                    Logger.log(Logger.Event.Failure("create_custom_course_submit"), "exception" to exception.message.orEmpty())

                    when (exception) {
                        is CancellationException -> {
                            throw exception
                        }

                        is NoNetworkException -> {
                            _event.emit(CreateCustomCourseUiEvent.NoNetwork)
                        }

                        is HttpException -> {
                            _event.emit(
                                when (exception.code()) {
                                    400 -> CreateCustomCourseUiEvent.InvalidCourseName
                                    401 -> CreateCustomCourseUiEvent.UnauthorizedUser
                                    409 -> CreateCustomCourseUiEvent.DuplicateCourseName
                                    else -> CreateCustomCourseUiEvent.UnknownError
                                },
                            )
                        }

                        else -> {
                            _event.emit(CreateCustomCourseUiEvent.UnknownError)
                        }
                    }
                }
            }
        }

        fun dismissAuthDialog() {
            _authDialogState.value = null
        }

        companion object {
            private val MINIMUM_COURSE_LENGTH = Length(1)
        }
    }
