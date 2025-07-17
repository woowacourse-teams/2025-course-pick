package io.coursepick.coursepick.view

import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemCourseBinding

class CourseViewHolder(
    private val binding: ItemCourseBinding,
    onSelectCourseListener: OnSelectCourseListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onSelectCourseListener = onSelectCourseListener
    }

    fun bind(course: CourseItem) {
        binding.course = course
    }
}

fun interface OnSelectCourseListener {
    fun select(course: CourseItem)
}
