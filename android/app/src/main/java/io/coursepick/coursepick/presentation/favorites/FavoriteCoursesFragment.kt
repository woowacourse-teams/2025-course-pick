package io.coursepick.coursepick.presentation.favorites

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.coursepick.coursepick.databinding.FragmentFavoriteCoursesBinding
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.course.CourseAdapter
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.course.CourseItemListener
import io.coursepick.coursepick.presentation.course.CoursesUiState
import io.coursepick.coursepick.presentation.course.CoursesViewModel

class FavoriteCoursesFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentFavoriteCoursesBinding? = null
    private val binding get() = _binding!!
    private val courseAdapter by lazy { CourseAdapter(CourseItemListener()) }
    private val viewModel: CoursesViewModel by activityViewModels { CoursesViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpBindingVariables()
        setUpStateObserver()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setUpBindingVariables() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.adapter = courseAdapter
    }

    private fun setUpStateObserver() {
        viewModel.state.observe(viewLifecycleOwner) { state: CoursesUiState ->
            courseAdapter.submitList(state.courses)
        }
    }

    private fun CourseItemListener(): CourseItemListener =
        object : CourseItemListener {
            override fun select(course: CourseItem) {
                Logger.log(
                    Logger.Event.Click("course_on_list"),
                    "id" to course.id,
                    "name" to course.name,
                )
                viewModel.select(course)
            }

            override fun toggleLike(course: CourseItem) {
                viewModel.toggleLike(course)
            }

            @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
            override fun navigateToMap(course: CourseItem) {
                Logger.log(
                    Logger.Event.Click("navigate"),
                    "id" to course.id,
                    "name" to course.name,
                )
            }
        }
}
