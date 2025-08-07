package io.coursepick.coursepick.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemCourseBinding

class CourseViewHolder private constructor(
    private val binding: ItemCourseBinding,
    onSelectCourseListener: OnSelectCourseListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onSelectCourseListener = onSelectCourseListener
    }

    fun bind(course: CourseItem) {
        binding.course = course
    }

    companion object {
        fun CourseViewHolder(
            root: ViewGroup,
            onSelectCourseListener: OnSelectCourseListener,
        ): CourseViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemCourseBinding.inflate(layoutInflater, root, false)
            return CourseViewHolder(binding, onSelectCourseListener)
        }
    }
}
