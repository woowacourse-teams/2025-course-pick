package io.coursepick.coursepick.presentation.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.coursepick.coursepick.databinding.DialogFilterBinding
import io.coursepick.coursepick.presentation.course.CoursesViewModel

class FilterBottomSheet : BottomSheetDialogFragment() {
    private val coursesViewModel: CoursesViewModel by activityViewModels()

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
        binding.viewModel = coursesViewModel
    }

    private fun setUpEventObserver() {
        coursesViewModel.filterEvent.observe(this) { event: FilterUiEvent ->
            when (event) {
                FilterUiEvent.ResetFilter -> Unit
                FilterUiEvent.CancelFilter -> dismiss()
                is FilterUiEvent.ApplyFilter -> dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
