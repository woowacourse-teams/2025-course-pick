package io.coursepick.coursepick.presentation.course

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import io.coursepick.coursepick.databinding.DialogCourseColorDescriptionBinding

class CourseColorDescriptionDialog : DialogFragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: DialogCourseColorDescriptionBinding? = null
    private val binding: DialogCourseColorDescriptionBinding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogCourseColorDescriptionBinding.inflate(inflater, container, false)
        binding.listener = OnConfirmListener { dismiss() }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
