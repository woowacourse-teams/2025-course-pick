package io.coursepick.coursepick.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.coursepick.coursepick.databinding.DialogMapChoiceBinding
import io.coursepick.coursepick.domain.Coordinate

class MapChoiceDialogFragment :
    DialogFragment(),
    OnChosenListener {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: DialogMapChoiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var origin: Coordinate
    private lateinit var destination: Coordinate
    private lateinit var destinationName: String
    private lateinit var state: MapChoiceUiState

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setUpProperties()
        _binding = DialogMapChoiceBinding.inflate(layoutInflater)
        setUpBindingVariables()

        return AlertDialog
            .Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onChosen(position: Int) {
        val selectedMap: MapApplication = state.mapApplications[position]

        if (state.defaultAppChecked) {
            CoursePickPreferences.selectedMapApplication = selectedMap
        }

        selectedMap.launch(
            requireContext(),
            origin,
            destination,
            destinationName,
        )

        dismiss()
    }

    private fun setUpProperties() {
        arguments?.let { arguments: Bundle ->
            origin = arguments.getSerializableCompat(ARGUMENT_ORIGIN)!!
            destination = arguments.getSerializableCompat(ARGUMENT_DESTINATION)!!
            destinationName = arguments.getString(ARGUMENT_DESTINATION_NAME)!!
            state = arguments.getSerializableCompat(ARGUMENT_STATE)!!
        }
    }

    private fun setUpBindingVariables() {
        binding.state = state
        binding.onChosenListener = this
    }

    companion object {
        private const val ARGUMENT_ORIGIN = "origin"
        private const val ARGUMENT_DESTINATION = "destination"
        private const val ARGUMENT_DESTINATION_NAME = "destination_name"
        private const val ARGUMENT_STATE = "state"

        fun newInstance(
            origin: Coordinate,
            destination: Coordinate,
            destinationName: String,
            state: MapChoiceUiState = MapChoiceUiState(),
        ): MapChoiceDialogFragment {
            val fragment = MapChoiceDialogFragment()
            val arguments: Bundle =
                Bundle().apply {
                    putSerializable(ARGUMENT_ORIGIN, origin)
                    putSerializable(ARGUMENT_DESTINATION, destination)
                    putString(ARGUMENT_DESTINATION_NAME, destinationName)
                    putSerializable(ARGUMENT_STATE, state)
                }
            fragment.arguments = arguments
            return fragment
        }
    }
}
