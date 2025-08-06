package io.coursepick.coursepick.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.coursepick.coursepick.domain.Place

class SearchAdapter(
    private val onSearchListener: OnSearchListener,
) : ListAdapter<Place, SearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchViewHolder = SearchViewHolder(parent, onSearchListener)

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<Place>() {
                override fun areItemsTheSame(
                    oldItem: Place,
                    newItem: Place,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Place,
                    newItem: Place,
                ): Boolean = oldItem == newItem
            }
    }
}
