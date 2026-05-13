package io.coursepick.coursepick.presentation.coursedetail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@AndroidEntryPoint
class CourseDetailActivity : ComponentActivity() {
    private val viewModel: CourseDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoursePickTheme {
                CourseDetailScreen(
                    courseDetail =
                        CourseDetail(
                            id = "",
                            courseName = CourseName("석촌호수 동호"),
                            length = Length(5678),
                            isFavorite = false,
                            reviewCount = 99,
                            averageRating = 4.32F,
                        ),
                    reviews =
                        List(10) { index: Int ->
                            Review(
                                id = index.toString(),
                                username = "달리는 런숭이 $index",
                                isMine = index == 0,
                                rating = 4 + index / 10F,
                                comment = "리뷰 내용 ".repeat(10 + index * 5),
                            )
                        },
                )
            }
        }
    }
}
