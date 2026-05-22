package io.coursepick.coursepick.presentation.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WriteCourseReviewViewModel
    @Inject
    constructor() : ViewModel() {
        private val _courseName = MutableStateFlow(CourseName("석촌호수 동호"))
        val courseName: StateFlow<CourseName> get() = _courseName.asStateFlow()

        private val _courseLength = MutableStateFlow(Length(5678))
        val courseLength: StateFlow<Length> get() = _courseLength.asStateFlow()

        private val _reviewContent = MutableStateFlow("")
        val reviewContent: StateFlow<String> get() = _reviewContent.asStateFlow()

        private val _rating = MutableStateFlow(0F)
        val rating: StateFlow<Float> get() = _rating.asStateFlow()

        val canSubmit: StateFlow<Boolean> =
            combine(rating, reviewContent) { rating: Float?, reviewContent: String ->
                rating != null && reviewContent.isNotBlank()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

        fun setRating(rating: Float) {
            _rating.value = rating
        }

        fun setReviewText(text: String) {
            _reviewContent.value = text.take(MAX_REVIEW_LENGTH)
        }

        companion object {
            const val MAX_REVIEW_LENGTH = 1_000
        }
    }
