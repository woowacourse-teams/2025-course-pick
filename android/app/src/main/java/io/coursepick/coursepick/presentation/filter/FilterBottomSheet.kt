package io.coursepick.coursepick.presentation.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.coursepick.coursepick.databinding.DialogFilterBinding
import io.coursepick.coursepick.presentation.compat.getParcelableCompat
import io.coursepick.coursepick.presentation.course.CoursesViewModel

class FilterBottomSheet : BottomSheetDialogFragment() {
    private val coursesViewModel: CoursesViewModel by activityViewModels()

    private val filterViewModel: FilterViewModel by viewModels {
        FilterViewModel.factory(
            arguments?.getParcelableCompat(ARG_COURSE_FILTER) ?: CourseFilter(),
            coursesViewModel.originalCourses,
        )
    }

    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        setUpBindingVariables()
        setUpEventObserver()

        return binding.root
    }

    private fun setUpBindingVariables() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = filterViewModel
        coursesViewModel.state.observe(viewLifecycleOwner) { courses ->
            if (courses.courses.isNotEmpty()) {
                filterViewModel.updateCourses(coursesViewModel.originalCourses)
            }
        }
    }

    private fun setUpEventObserver() {
        filterViewModel.event.observe(this) { event: FilterUiEvent ->
            when (event) {
                FilterUiEvent.FilterCancel -> dismiss()
                FilterUiEvent.FilterResult -> {
                    val courseFilter =
                        filterViewModel.state.value?.toCourseFilter() ?: CourseFilter()
                    parentFragmentManager.setFragmentResult(
                        "course_filter_request",
                        bundleOf("courseFilter" to courseFilter),
                    )
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_COURSE_FILTER = "course_filter"

        fun newInstance(courseFilter: CourseFilter): FilterBottomSheet =
            FilterBottomSheet().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(ARG_COURSE_FILTER, courseFilter)
                    }
            }
    }
}
