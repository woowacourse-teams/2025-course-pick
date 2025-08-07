package io.coursepick.coursepick.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemCourseBinding

class CourseViewHolder private constructor(
    private val binding: ItemCourseBinding,
    courseItemListener: CourseItemListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.courseItemListener = courseItemListener
    }

    fun bind(course: CourseItem) {
        binding.course = course
    }

    companion object {
        operator fun invoke(
            root: ViewGroup,
            courseItemListener: CourseItemListener,
        ): CourseViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemCourseBinding.inflate(layoutInflater, root, false)
            return CourseViewHolder(binding, courseItemListener)
        }
    }
}
