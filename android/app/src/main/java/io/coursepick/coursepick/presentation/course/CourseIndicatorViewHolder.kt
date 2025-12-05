package io.coursepick.coursepick.presentation.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemIndicatorBinding

class CourseIndicatorViewHolder private constructor(
    binding: ItemIndicatorBinding,
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        operator fun invoke(root: ViewGroup): CourseIndicatorViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemIndicatorBinding.inflate(layoutInflater, root, false)
            return CourseIndicatorViewHolder(binding)
        }
    }
}
