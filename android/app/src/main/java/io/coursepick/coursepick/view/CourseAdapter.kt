package io.coursepick.coursepick.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.coursepick.coursepick.databinding.ItemCourseBinding

class CourseAdapter(
    private val onSelectCourseListener: OnSelectCourseListener,
) : ListAdapter<CourseItem, CourseViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CourseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCourseBinding.inflate(layoutInflater, parent, false)
        return CourseViewHolder(binding, onSelectCourseListener)
    }

    override fun onBindViewHolder(
        holder: CourseViewHolder,
        position: Int,
    ) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<CourseItem>() {
                override fun areItemsTheSame(
                    oldItem: CourseItem,
                    newItem: CourseItem,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: CourseItem,
                    newItem: CourseItem,
                ): Boolean = oldItem == newItem
            }
    }
}
