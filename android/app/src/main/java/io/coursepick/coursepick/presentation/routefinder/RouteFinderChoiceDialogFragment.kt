package io.coursepick.coursepick.presentation.routefinder

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.coursepick.coursepick.databinding.DialogRouteFinderChoiceBinding
import io.coursepick.coursepick.presentation.DataKeys
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences

class RouteFinderChoiceDialogFragment :
    DialogFragment(),
    OnChosenListener {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: DialogRouteFinderChoiceBinding? = null
    private val binding get() = _binding!!
    private val state: RouteFinderChoiceUiState = RouteFinderChoiceUiState()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRouteFinderChoiceBinding.inflate(layoutInflater)
        setUpBindingVariables()

        return AlertDialog.Builder(requireContext()).setView(binding.root).create()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onChosen(position: Int) {
        val selectedMap: RouteFinderApplication = state.routeFinders[position]

        if (state.defaultAppChecked) {
            CoursePickPreferences.selectedRouteFinder = selectedMap
        }

        val result: Bundle =
            Bundle().apply {
                putParcelable(
                    DataKeys.DATA_KEY_ROUTE_FINDER_CHOICE_RESULT,
                    selectedMap,
                )
            }
        parentFragmentManager.setFragmentResult(
            DataKeys.DATA_KEY_ROUTE_FINDER_CHOICE_REQUEST,
            result,
        )
        dismiss()
    }

    private fun setUpBindingVariables() {
        binding.state = state
        binding.onChosenListener = this
    }
}
