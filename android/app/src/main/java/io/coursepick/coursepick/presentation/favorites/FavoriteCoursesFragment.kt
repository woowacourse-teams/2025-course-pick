package io.coursepick.coursepick.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.coursepick.coursepick.databinding.FragmentFavoriteCoursesBinding
import io.coursepick.coursepick.presentation.course.CourseAdapter
import io.coursepick.coursepick.presentation.course.CourseItemListener
import io.coursepick.coursepick.presentation.course.CoursesUiState
import io.coursepick.coursepick.presentation.course.CoursesViewModel

class FavoriteCoursesFragment(
    listener: CourseItemListener,
) : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentFavoriteCoursesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoursesViewModel by activityViewModels { CoursesViewModel.Factory }
    private val courseAdapter by lazy { CourseAdapter(listener) }

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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.adapter = courseAdapter
    }

    private fun setUpStateObserver() {
        viewModel.state.observe(viewLifecycleOwner) { state: CoursesUiState ->
            courseAdapter.submitList(state.courses)
        }
    }
}
