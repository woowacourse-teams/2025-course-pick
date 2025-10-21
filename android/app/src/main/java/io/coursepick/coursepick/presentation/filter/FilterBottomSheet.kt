package io.coursepick.coursepick.presentation.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.coursepick.coursepick.databinding.DialogFilterBinding
import io.coursepick.coursepick.presentation.course.CoursesViewModel

class FilterBottomSheet :
    BottomSheetDialogFragment(),
    FilterAction {
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

        return binding.root
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
        coursesViewModel
        dismiss()
    }

    override fun reset() {
        coursesViewModel.resetFilterToDefault()
    }

    override fun apply() {
        dismiss()
    }
}
