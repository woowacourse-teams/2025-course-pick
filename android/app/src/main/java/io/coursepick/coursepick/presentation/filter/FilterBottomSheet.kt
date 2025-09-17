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
import io.coursepick.coursepick.R
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

        val lengthMinimum =
            filterViewModel.uiState.value
                ?.lengthMinimum
                ?.toFloat() ?: MINIMUM_LENGTH_RANGE
        val lengthMaximum =
            filterViewModel.uiState.value
                ?.lengthMaximum
                ?.toFloat() ?: MAXIMUM_LENGTH_RANGE

        val slider = binding.mainFilterLengthSlider
        slider.valueFrom = lengthMinimum
        slider.valueTo = lengthMaximum
        slider.stepSize = STEP_SIZE
        slider.values = listOf(MINIMUM_LENGTH_RANGE, MAXIMUM_LENGTH_RANGE)

        setSlider(lengthMinimum, lengthMaximum)

        slider.addOnChangeListener { _, _, _ ->
            val values = slider.values
            val min = values[0]
            val max = values[1]
            setSlider(min, max)
        }

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    private fun setSlider(
        minimum: Float = MINIMUM_LENGTH_RANGE,
        maximum: Float = MAXIMUM_LENGTH_RANGE,
    ) {
        binding.mainFilterLengthSlider.values = listOf(minimum, maximum)
        if (minimum == MINIMUM_LENGTH_RANGE && maximum != MAXIMUM_LENGTH_RANGE) {
            binding.mainFilterLengthRange.text =
                getString(R.string.length_range_open_start, maximum.toInt())
        } else if (minimum != MINIMUM_LENGTH_RANGE && maximum == MAXIMUM_LENGTH_RANGE) {
            binding.mainFilterLengthRange.text =
                getString(R.string.length_range_open_end, minimum.toInt())
        } else if (minimum != MINIMUM_LENGTH_RANGE && maximum != MAXIMUM_LENGTH_RANGE) {
            binding.mainFilterLengthRange.text =
                getString(R.string.length_range, minimum.toInt(), maximum.toInt())
        } else {
            binding.mainFilterLengthRange.text = getString(R.string.total_length_range)
        }
        filterViewModel.recalcFilteredCourses()
    }

    companion object {
        private const val ARG_CONDITION = "filter_condition"

        private const val MINIMUM_LENGTH_RANGE = 0f
        private const val MAXIMUM_LENGTH_RANGE = 21f
        private const val STEP_SIZE: Float = 1f

        fun newInstance(condition: FilterCondition): FilterBottomSheet =
            FilterBottomSheet().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(ARG_CONDITION, condition)
                    }
            }
    }
}
