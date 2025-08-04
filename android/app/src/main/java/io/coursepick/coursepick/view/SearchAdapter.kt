package io.coursepick.coursepick.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.coursepick.coursepick.domain.SearchPlace
import io.coursepick.coursepick.view.SearchViewHolder.Companion.SearchViewHolder

class SearchAdapter(
    private val onSearchKeywordListener: OnSearchKeywordListener,
) : ListAdapter<SearchPlace, SearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchViewHolder = SearchViewHolder(parent, onSearchKeywordListener)

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<SearchPlace>() {
                override fun areItemsTheSame(
                    oldItem: SearchPlace,
                    newItem: SearchPlace,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: SearchPlace,
                    newItem: SearchPlace,
                ): Boolean = oldItem == newItem
            }
    }
}
