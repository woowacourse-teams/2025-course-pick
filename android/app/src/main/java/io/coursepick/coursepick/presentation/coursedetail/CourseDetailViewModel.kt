package io.coursepick.coursepick.presentation.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.favorites.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel
    @Inject
    constructor(
        private val favoritesRepository: FavoritesRepository,
    ) : ViewModel() {
        private val _course = MutableStateFlow(COURSE_FIXTURE)
        val course: StateFlow<Course> get() = _course.asStateFlow()

        private val _averageRating = MutableStateFlow(3.45F)
        val averageRating: StateFlow<Float> get() = _averageRating.asStateFlow()

        private val _reviewCount = MutableStateFlow(99)
        val reviewCount: StateFlow<Int> get() = _reviewCount.asStateFlow()

        val isFavorite: StateFlow<Boolean> =
            course
                .map { course: Course -> favoritesRepository.favoriteCourseIds().contains(course.id) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = false,
                )

        private val _reviews =
            MutableStateFlow(
                List(10) { index: Int ->
                    REVIEW_FIXTURE.copy(
                        id = REVIEW_FIXTURE.id + "_$index",
                        username = REVIEW_FIXTURE.username + "_$index",
                        isMine = index == 0,
                        rating = (REVIEW_FIXTURE.rating ?: 0F) + index / 10F,
                        comment = REVIEW_FIXTURE.comment.orEmpty().repeat(index + 1),
                    )
                },
            )
        val reviews: StateFlow<List<Review>> get() = _reviews.asStateFlow()

        fun deleteReview(review: Review) {
            // TODO: 구현 예정
        }

        fun reportReview(review: Review) {
            // TODO: 구현 예정
        }

        fun onWriteReview() {
            // TODO: 구현 예정
        }

        companion object {
            private val COURSE_FIXTURE =
                Course(
                    id = "course_fixture",
                    name = CourseName("석촌호수 동호"),
                    distance = Distance(1234),
                    length = Length(5678),
                    coordinates = List(2) { Coordinate(Latitude(0.0), Longitude(0.0)) },
                )

            private val REVIEW_FIXTURE =
                Review(
                    id = "review_fixture",
                    username = "달리는 런숭이",
                    isMine = false,
                    rating = 3F,
                    comment = "리뷰 내용 ",
                )
        }
    }
