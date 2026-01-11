package io.coursepick.coursepick.presentation.customcourse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.coursepick.coursepick.databinding.FragmentCustomCoursesBinding
import io.coursepick.coursepick.presentation.course.CoursesViewModel

class CustomCourseFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCustomCoursesBinding? = null
    private val binding: FragmentCustomCoursesBinding get() = _binding!!
    private val viewModel: CoursesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCustomCoursesBinding.inflate(inflater, container, false)
        binding.customCourse.setContent {
            CustomCourseScreen(
                uiState = CustomCourseUiState(emptyList()),
                onClick = { viewModel.showCreateCustomCourse() },
            )
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
