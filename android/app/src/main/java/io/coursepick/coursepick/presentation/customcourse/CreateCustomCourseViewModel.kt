package io.coursepick.coursepick.presentation.customcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.customcourse.CustomCourseRepository
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
        private val _event = MutableSharedFlow<CustomCourseUiEvent>()
        val event: SharedFlow<CustomCourseUiEvent> get() = _event.asSharedFlow()

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
                val newSegment: DraftSegment =
                    repository.draftSegment(waypoints.lastOrNull(), waypoint)
                if (newSegment.coordinates.isEmpty()) return@launch

                _segments.value += newSegment
                _event.emit(CustomCourseUiEvent.NewSegment(newSegment))
            }
        }

        fun removeLastWaypoint() {
            viewModelScope.launch {
                _segments.value = segments.value.dropLast(1)
                _event.emit(CustomCourseUiEvent.RemoveLastWaypoint)
            }
        }
    }
