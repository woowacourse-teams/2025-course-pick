package io.coursepick.coursepick.presentation.customcourse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.fragment.app.Fragment
import io.coursepick.coursepick.databinding.FragmentCustomCoursesBinding
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

class CustomCourseFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCustomCoursesBinding? = null
    private val binding: FragmentCustomCoursesBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCustomCoursesBinding.inflate(inflater, container, false)
        binding.customCourse.setContent {
            CoursePickTheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { Log.d("TAG", "onCreateView: click - FAB") },
                            shape = CircleShape,
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "추가")
                        }
                    },
                ) {
                    CustomCourseScreen(CustomCourseUiState(listOf()))
                }
            }
        }
        return binding.root
    }
}
