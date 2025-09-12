package io.coursepick.coursepick.presentation.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.coursepick.coursepick.R
import io.coursepick.coursepick.databinding.DialogFilterBinding
import io.coursepick.coursepick.domain.course.Difficulty
import io.coursepick.coursepick.domain.course.LengthRange

class FilterBottomSheet : BottomSheetDialogFragment() {
    private val viewModel: CoursesViewModel by activityViewModels()
    private var _binding: DialogFilterBinding? = null
    val binding get() = _binding!!

    private var difficulties = mutableSetOf<Difficulty>()
    private var lengthMinimum = MINIMUM_LENGTH_RANGE
    private var lengthMaximum = MAXIMUM_LENGTH_RANGE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFilterBinding.inflate(inflater, container, false)

        difficulties = viewModel.state.value
            ?.filterCondition
            ?.difficulties
            ?.toMutableSet() ?: mutableSetOf()

        lengthMinimum =
            viewModel.state.value
                ?.filterCondition
                ?.lengthRange
                ?.minimum
                ?.div(1000) ?: MINIMUM_LENGTH_RANGE
        lengthMaximum =
            viewModel.state.value
                ?.filterCondition
                ?.lengthRange
                ?.maximum
                ?.let { if (it == Int.MAX_VALUE) MAXIMUM_LENGTH_RANGE else it.div(1000) }
                ?: MAXIMUM_LENGTH_RANGE

        setupDifficultyButtons()

        difficulties.forEach {
            when (it) {
                Difficulty.EASY ->
                    binding.mainDifficultyEasy.setBackgroundResource(
                        R.drawable.background_difficulty_selected,
                    )

                Difficulty.NORMAL ->
                    binding.mainDifficultyNormal.setBackgroundResource(
                        R.drawable.background_difficulty_selected,
                    )

                Difficulty.HARD ->
                    binding.mainDifficultyHard.setBackgroundResource(
                        R.drawable.background_difficulty_selected,
                    )
            }
        }

        val slider = binding.mainFilterLengthSlider
        slider.valueFrom = MINIMUM_LENGTH_RANGE.toFloat()
        slider.valueTo = MAXIMUM_LENGTH_RANGE.toFloat()
        slider.stepSize = STEP_SIZE
        slider.values = listOf(lengthMinimum.toFloat(), lengthMaximum.toFloat())

        setSlider(lengthMinimum, lengthMaximum)

        slider.addOnChangeListener { _, _, _ ->
            val values = slider.values
            val min = values[0].toInt()
            val max = values[1].toInt()
            setSlider(min, max)
        }

        binding.mainFilterLengthConfirm.setOnClickListener {
            val min = slider.values[0].toInt() * 1000
            val max =
                if (slider.values[1].toInt() == 21) Int.MAX_VALUE else slider.values[1].toInt() * 1000
            confirmSelection(min, max)
        }

        binding.mainFilterLengthCancel.setOnClickListener {
            dismiss()
        }

        binding.mainFilterReset.setOnClickListener {
            difficulties.clear()
            binding.mainDifficultyEasy.setBackgroundResource(R.drawable.background_difficulty_default)
            binding.mainDifficultyNormal.setBackgroundResource(R.drawable.background_difficulty_default)
            binding.mainDifficultyHard.setBackgroundResource(R.drawable.background_difficulty_default)
            setSlider()
            viewModel.filter(FilterCondition())
        }

        return binding.root
    }

    private fun setSlider(
        minimum: Int = MINIMUM_LENGTH_RANGE,
        maximum: Int = MAXIMUM_LENGTH_RANGE,
    ) {
        binding.mainFilterLengthSlider.values = listOf(minimum.toFloat(), maximum.toFloat())
        if (minimum == MINIMUM_LENGTH_RANGE && maximum != MAXIMUM_LENGTH_RANGE) {
            binding.mainFilterLengthRange.text =
                getString(R.string.length_range_open_start, maximum)
        } else if (minimum != MINIMUM_LENGTH_RANGE && maximum == MAXIMUM_LENGTH_RANGE) {
            binding.mainFilterLengthRange.text = getString(R.string.length_range_open_end, minimum)
        } else if (minimum != MINIMUM_LENGTH_RANGE && maximum != MAXIMUM_LENGTH_RANGE) {
            binding.mainFilterLengthRange.text = getString(R.string.length_range, minimum, maximum)
        } else {
            binding.mainFilterLengthRange.text = getString(R.string.total_length_range)
        }
    }

    private fun setupDifficultyButtons() {
        binding.mainDifficultyEasy.setOnClickListener {
            if (difficulties.contains(Difficulty.EASY)) {
                difficulties.remove(Difficulty.EASY)
                binding.mainDifficultyEasy.setBackgroundResource(
                    R.drawable.background_difficulty_default,
                )
            } else {
                difficulties.add(Difficulty.EASY)
                binding.mainDifficultyEasy.setBackgroundResource(
                    R.drawable.background_difficulty_selected,
                )
            }
        }
        binding.mainDifficultyNormal.setOnClickListener {
            if (difficulties.contains(Difficulty.NORMAL)) {
                difficulties.remove(Difficulty.NORMAL)
                binding.mainDifficultyNormal.setBackgroundResource(
                    R.drawable.background_difficulty_default,
                )
            } else {
                difficulties.add(Difficulty.NORMAL)
                binding.mainDifficultyNormal.setBackgroundResource(
                    R.drawable.background_difficulty_selected,
                )
            }
        }
        binding.mainDifficultyHard.setOnClickListener {
            if (difficulties.contains(Difficulty.HARD)) {
                difficulties.remove(Difficulty.HARD)
                binding.mainDifficultyHard.setBackgroundResource(
                    R.drawable.background_difficulty_default,
                )
            } else {
                difficulties.add(Difficulty.HARD)
                binding.mainDifficultyHard.setBackgroundResource(
                    R.drawable.background_difficulty_selected,
                )
            }
        }
    }

    private fun confirmSelection(
        minimum: Int,
        maximum: Int,
    ) {
        val condition =
            FilterCondition(
                difficulties = difficulties.toSet(),
                lengthRange = LengthRange(minimum, maximum),
            )
        viewModel.filter(condition)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MINIMUM_LENGTH_RANGE: Int = 0
        private const val MAXIMUM_LENGTH_RANGE: Int = 21
        private const val STEP_SIZE: Float = 1f
    }
}
