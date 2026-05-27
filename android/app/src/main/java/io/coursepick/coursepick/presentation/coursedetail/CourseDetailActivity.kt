package io.coursepick.coursepick.presentation.coursedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@AndroidEntryPoint
class CourseDetailActivity : ComponentActivity() {
    private val courseDetailViewModel: CourseDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val courseId: String =
            intent.getStringExtra(KEY_COURSE_ID) ?: run {
                onLoadFailure()
                return
            }

        setContent {
            CoursePickTheme {
                val backstack: NavBackStack<NavKey> = rememberNavBackStack(CourseDetailRoute.CourseDetail)
                val entryProvider: (NavKey) -> NavEntry<NavKey> =
                    entryProvider {
                        entry<CourseDetailRoute.CourseDetail> {
                            CourseDetailScreen(
                                courseId = courseId,
                                onNavigateBack = ::finish,
                                onWriteReview = { courseDetail: CourseDetailUiModel ->
                                    backstack.add(CourseDetailRoute.WriteReview(courseDetail))
                                },
                                courseDetailViewModel = courseDetailViewModel,
                            )
                        }

                        entry<CourseDetailRoute.WriteReview> { key: CourseDetailRoute.WriteReview ->
                            WriteCourseReviewScreen(
                                courseDetail = key.courseDetail,
                                onComplete = {
                                    backstack.removeLastOrNull()
                                    courseDetailViewModel.load(courseId)
                                },
                            )
                        }
                    }

                NavDisplay(
                    backStack = backstack,
                    onBack = backstack::removeLastOrNull,
                    entryProvider = entryProvider,
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { fullWidth: Int -> fullWidth }) togetherWith
                            slideOutHorizontally(targetOffsetX = { fullWidth: Int -> -fullWidth })
                    },
                    popTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { fullWidth: Int -> -fullWidth }) togetherWith
                            slideOutHorizontally(targetOffsetX = { fullWidth: Int -> fullWidth })
                    },
                )
            }
        }
    }

    private fun onLoadFailure() {
        Toast.makeText(this@CourseDetailActivity, getString(R.string.course_detail_load_failure_message), Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        private const val KEY_COURSE_ID = "course_id"

        fun intent(
            context: Context,
            courseId: String,
        ): Intent = Intent(context, CourseDetailActivity::class.java).putExtra(KEY_COURSE_ID, courseId)
    }
}
