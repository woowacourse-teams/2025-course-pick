package io.coursepick.coursepick.presentation.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import io.coursepick.coursepick.databinding.FragmentExploreCoursesBinding
import io.coursepick.coursepick.presentation.filter.CourseFilterBottomSheet
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

class ExploreCoursesFragment(
    listener: CourseItemListener,
) : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentExploreCoursesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoursesViewModel by activityViewModels()
    private val courseAdapter by lazy { CourseAdapter(listener) }
    private var showFilterDialog by mutableStateOf(false)

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
        binding.composeView.setContent {
            CoursePickTheme {
                val uiState by viewModel.state.observeAsState()
                if (showFilterDialog && uiState != null) {
                    CourseFilterBottomSheet(
                        coursesUiState = uiState!!,
                        onDismissRequest = { showFilterDialog = false },
                        onRangeSliderValueChange = { range ->
                            viewModel.updateLengthRange(
                                range.start.toDouble(),
                                range.endInclusive.toDouble(),
                            )
                        },
                        onCancel = {
                            viewModel.restoreState()
                            showFilterDialog = false
                        },
                    )
                }
            }
        }
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
        binding.viewModel = viewModel
        binding.showFiltersListener = this
    }

    private fun setUpStateObserver() {
        viewModel.state.observe(viewLifecycleOwner) { state: CoursesUiState ->
            courseAdapter.submitList(state.courses)
        }
    }

    override fun showFilters() {
        viewModel.backupState()
        showFilterDialog = true
    }

    fun scrollTo(courseItem: CourseItem) {
        val position =
            courseAdapter.currentList.indexOfFirst { item: CourseItem -> item.id == courseItem.id }
        if (position == -1) return
        val layoutManager = binding.mainCourses.layoutManager as? LinearLayoutManager ?: return
        val smoothScroller =
            object : LinearSmoothScroller(requireContext()) {
                override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            }
        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
    }
}
