package io.coursepick.coursepick.presentation.customcourse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

class CreateCustomCourseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoursePickTheme {
                CreateCustomCourseScreen(onClose = { finish() })
            }
        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, CreateCustomCourseActivity::class.java)
    }
}
