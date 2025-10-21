package io.coursepick.coursepick.presentation.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.coursepick.coursepick.databinding.FragmentExploreCoursesBinding
import io.coursepick.coursepick.presentation.filter.FilterBottomSheet

class ExploreCoursesFragment(
    listener: CourseItemListener,
) : Fragment(),
    ShowFiltersListener {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentExploreCoursesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoursesViewModel by activityViewModels { CoursesViewModel.Factory }
    private val courseAdapter by lazy { CourseAdapter(listener) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExploreCoursesBinding.inflate(inflater, container, false)
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.adapter = courseAdapter
        binding.showFiltersListener = this
    }

    private fun setUpStateObserver() {
        viewModel.state.observe(viewLifecycleOwner) { state: CoursesUiState ->
            courseAdapter.submitList(state.courses)
        }
    }

    override fun showFilters() {
        val dialog = FilterBottomSheet()
        dialog.show(childFragmentManager, null)
    }

    fun scrollTo(courseItem: CourseItem) {
        val position =
            courseAdapter.currentList.indexOfFirst { item: CourseItem -> item.id == courseItem.id }
        if (position == -1) return
        binding.mainCourses.smoothScrollToPosition(position)
        binding.mainCourses.postDelayed({
            val layoutManager =
                binding.mainCourses.layoutManager as? LinearLayoutManager ?: return@postDelayed
            layoutManager.scrollToPositionWithOffset(position, 0)
        }, 100)
    }
}
