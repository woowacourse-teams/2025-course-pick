package io.coursepick.coursepick.presentation.coursedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@AndroidEntryPoint
class CourseDetailActivity : ComponentActivity() {
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
                                onNavigateBack = ::finish,
                                onWriteReview = { backstack.add(CourseDetailRoute.WriteReview) },
                            )
                        }

                        entry<CourseDetailRoute.WriteReview> {
                            WriteCourseReviewScreen()
                        }
                    }

                NavDisplay(
                    backStack = backstack,
                    onBack = backstack::removeLastOrNull,
                    entryProvider = entryProvider,
                )
            }
        }
    }

    private fun onLoadFailure() {
        Toast.makeText(this@CourseDetailActivity, "코스 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
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
