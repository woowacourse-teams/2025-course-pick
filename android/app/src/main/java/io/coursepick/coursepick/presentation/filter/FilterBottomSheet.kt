package io.coursepick.coursepick.presentation.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.coursepick.coursepick.databinding.DialogFilterBinding
import io.coursepick.coursepick.presentation.course.CoursesViewModel

class FilterBottomSheet : BottomSheetDialogFragment() {
    private val parentViewModel: CoursesViewModel by activityViewModels()

    private val filterViewModel: FilterViewModel by viewModels {
        val condition = arguments?.getParcelable(ARG_CONDITION) ?: FilterCondition()
        val allCourses = parentViewModel.state.value?.courses ?: emptyList()
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = FilterViewModel(condition, allCourses) as T
        }
    }

    private var _binding: DialogFilterBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            DialogFilterBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = filterViewModel
            }

        binding.mainFilteredCoursesResult.setOnClickListener {
            val condition = filterViewModel.toCondition()
            parentViewModel.applyfilter(condition)
            dismiss()
        }

        return binding.root
    }

    companion object {
        private const val ARG_CONDITION = "filter_condition"

        fun newInstance(condition: FilterCondition): FilterBottomSheet =
            FilterBottomSheet().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(ARG_CONDITION, condition)
                    }
            }
    }
}
