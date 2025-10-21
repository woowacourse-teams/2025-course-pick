package io.coursepick.coursepick.presentation.filter

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.coursepick.coursepick.databinding.DialogFilterBinding
import io.coursepick.coursepick.presentation.course.CoursesUiState
import io.coursepick.coursepick.presentation.course.CoursesViewModel

class FilterBottomSheet :
    BottomSheetDialogFragment(),
    FilterAction {
    private val coursesViewModel: CoursesViewModel by activityViewModels()
    private lateinit var committedState: CoursesUiState

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
        val state = coursesViewModel.state.value
        if (state == null) {
            dismiss()
            return binding.root
        }
        committedState = state

        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        coursesViewModel.restore(committedState)
    }

    private fun setUpBindingVariables() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = coursesViewModel
        binding.action = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun cancel() {
        coursesViewModel.restore(committedState)
        dismiss()
    }

    override fun reset() {
        coursesViewModel.resetFilterToDefault()
    }

    override fun apply() {
        dismiss()
    }
}
