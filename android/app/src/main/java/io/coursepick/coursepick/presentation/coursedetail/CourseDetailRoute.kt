package io.coursepick.coursepick.presentation.coursedetail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface CourseDetailRoute : NavKey {
    @Serializable
    data object CourseDetail : CourseDetailRoute

    @Serializable
    data class WriteReview(
        val courseDetail: CourseDetailUiModel,
    ) : CourseDetailRoute
}
