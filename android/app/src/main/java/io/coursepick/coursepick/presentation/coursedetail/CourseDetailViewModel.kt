package io.coursepick.coursepick.presentation.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.interceptor.NoNetworkException
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.CourseRepository
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.favorites.FavoritesRepository
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
class CourseDetailViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val courseRepository: CourseRepository,
        private val favoritesRepository: FavoritesRepository,
    ) : ViewModel() {
        private val _course = MutableStateFlow(COURSE_FIXTURE)
        val course: StateFlow<Course> get() = _course.asStateFlow()

        private val _averageRating = MutableStateFlow(3.45F)
        val averageRating: StateFlow<Float> get() = _averageRating.asStateFlow()

        private val _reviewCount = MutableStateFlow(99)
        val reviewCount: StateFlow<Int> get() = _reviewCount.asStateFlow()

        private val _showReportCourseDialog = MutableStateFlow(false)
        val showReportCourseDialog: StateFlow<Boolean> get() = _showReportCourseDialog.asStateFlow()

        private val _authDialogState = MutableStateFlow<AuthFeature?>(null)
        val authDialogState: StateFlow<AuthFeature?> get() = _authDialogState.asStateFlow()

        private val _event = MutableSharedFlow<CourseDetailEvent>()
        val event: SharedFlow<CourseDetailEvent> get() = _event.asSharedFlow()

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
                        rating = REVIEW_FIXTURE.rating + index / 10F,
                        comment = REVIEW_FIXTURE.comment.repeat(index + 1),
                    )
                },
            )
        val reviews: StateFlow<List<Review>> get() = _reviews.asStateFlow()

        fun onReportCourse(course: Course) {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _authDialogState.value = AuthFeature.ReportCourse(course)
                } else {
                    _showReportCourseDialog.value = true
                }
            }
        }

        fun submitCourseReport(course: Course) {
            viewModelScope.launch {
                try {
                    courseRepository.report(course)
                    dismissReportCourseDialog()
                    _event.emit(CourseDetailEvent.ReportCourseSuccess)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: NoNetworkException) {
                    _event.emit(CourseDetailEvent.NoNetwork)
                } catch (exception: HttpException) {
                    _event.emit(
                        when (exception.code()) {
                            400 -> {
                                dismissReportCourseDialog()
                                CourseDetailEvent.CourseAlreadyReported
                            }

                            401 -> {
                                CourseDetailEvent.ReportCourseUnauthorizedUser
                            }

                            else -> {
                                CourseDetailEvent.ReportCourseUnknownFailure
                            }
                        },
                    )
                } catch (_: Throwable) {
                    _event.emit(CourseDetailEvent.ReportCourseUnknownFailure)
                }
            }
        }

        fun dismissReportCourseDialog() {
            _showReportCourseDialog.value = false
        }

        fun dismissAuthDialog() {
            _authDialogState.value = null
        }

        fun onAuthSuccess(feature: AuthFeature) {
            dismissAuthDialog()
            if (feature is AuthFeature.ReportCourse) {
                onReportCourse(feature.course)
            }
        }

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
