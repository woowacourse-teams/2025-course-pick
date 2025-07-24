package io.coursepick.coursepick.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemCourseBinding

class CourseViewHolder private constructor(
    private val binding: ItemCourseBinding,
    selectCourseListener: SelectCourseListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onSelectCourseListener = selectCourseListener
    }

    fun bind(course: CourseItem) {
        binding.course = course
    }

    companion object {
        fun CourseViewHolder(
            root: ViewGroup,
            selectCourseListener: SelectCourseListener,
        ): CourseViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemCourseBinding.inflate(layoutInflater, root, false)
            return CourseViewHolder(binding, selectCourseListener)
        }
    }
}
