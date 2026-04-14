package io.coursepick.coursepick.presentation.customcourse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import io.coursepick.coursepick.databinding.FragmentCustomCoursesBinding
import io.coursepick.coursepick.presentation.createcustomcourse.CoordinateUiModel
import io.coursepick.coursepick.presentation.createcustomcourse.CreateCustomCourseActivity
import io.coursepick.coursepick.presentation.map.MapManager

class CustomCoursesFragment(
    private val mapManager: MapManager,
) : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCustomCoursesBinding? = null
    private val binding: FragmentCustomCoursesBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCustomCoursesBinding.inflate(inflater, container, false)
        binding.customCourses.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CustomCourseScreen(onClick = { navigateCreateCustomCourse() })
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun navigateCreateCustomCourse() {
        val intent =
            CreateCustomCourseActivity.intent(
                requireContext(),
                mapManager.cameraCoordinate?.let(::CoordinateUiModel),
            )
        startActivity(intent)
    }
}
