package io.coursepick.coursepick.presentation.coursedetail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@AndroidEntryPoint
class CourseDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoursePickTheme {
                CourseDetailScreen()
            }
        }
    }
}
