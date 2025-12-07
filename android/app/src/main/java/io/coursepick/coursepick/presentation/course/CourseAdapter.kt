package io.coursepick.coursepick.presentation.course

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.R

class CourseAdapter(
    private val courseItemListener: CourseItemListener,
) : ListAdapter<CourseListItem, RecyclerView.ViewHolder>(diffUtil) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is CourseListItem.Course -> R.layout.item_course
            is CourseListItem.Loading -> R.layout.item_loading
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.item_course -> CourseViewHolder(parent, courseItemListener)
            R.layout.item_loading -> LoadingViewHolder(parent)
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item: CourseListItem = getItem(position)) {
            is CourseListItem.Course -> (holder as CourseViewHolder).bind(item.item)
            is CourseListItem.Loading -> Unit
        }
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<CourseListItem>() {
                override fun areItemsTheSame(
                    oldItem: CourseListItem,
                    newItem: CourseListItem,
                ): Boolean =
                    when {
                        oldItem is CourseListItem.Course && newItem is CourseListItem.Course ->
                            oldItem.item.id == newItem.item.id

                        oldItem is CourseListItem.Loading && newItem is CourseListItem.Loading ->
                            true

                        else -> false
                    }

                override fun areContentsTheSame(
                    oldItem: CourseListItem,
                    newItem: CourseListItem,
                ): Boolean =
                    when {
                        oldItem is CourseListItem.Course && newItem is CourseListItem.Course ->
                            oldItem.item == newItem.item

                        oldItem is CourseListItem.Loading && newItem is CourseListItem.Loading ->
                            true

                        else -> false
                    }
            }
    }
}
