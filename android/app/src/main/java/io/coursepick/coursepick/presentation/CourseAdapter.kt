package io.coursepick.coursepick.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class CourseAdapter(
    private val courseItemListener: CourseItemListener,
) : ListAdapter<CourseItem, CourseViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CourseViewHolder = CourseViewHolder(parent, courseItemListener)

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
